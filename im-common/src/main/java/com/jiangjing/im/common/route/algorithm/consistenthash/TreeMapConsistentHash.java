package com.jiangjing.im.common.route.algorithm.consistenthash;

import com.jiangjing.im.common.enums.UserErrorCode;
import com.jiangjing.im.common.exception.ApplicationException;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 使用 TreeMap 实现 Hash 环
 *
 * @author Admin
 */
public class TreeMapConsistentHash extends AbstractConsistentHash {

    private final TreeMap<Long, String> treeMap = new TreeMap<>();

    /**
     * 虚拟节点的个数
     */
    private static final int NODE_SIZE = 10;

    @Override
    protected void add(long hash, String value) {
        for (int i = 0; i < NODE_SIZE; i++) {
            // 构建虚拟节点
            treeMap.put(super.hash("node_" + hash + "_" + i), value);
        }
        treeMap.put(hash, value);
    }

    /**
     * 实现一致性 Hash 算法的逻辑
     *
     * @param key
     * @return
     */
    @Override
    protected String get(String key) {
        Long hash = super.hash(key);

        if (treeMap.isEmpty()) {
            throw new ApplicationException(UserErrorCode.SERVER_NOT_AVAILABLE);
        }
        // 获取 treeMap 中大于执行 key 值的所有节点信息
        SortedMap<Long, String> longStringSortedMap = treeMap.tailMap(hash);
        if (!longStringSortedMap.isEmpty()) {
            // 存在大于该hash值的节点
            return longStringSortedMap.get(longStringSortedMap.firstKey());
        }
        // 不存在大于该 Hash 值的节点，那么这回到 Hash 环的第一个节点
        return treeMap.firstEntry().getValue();
    }

    /**
     * 每次获取节点前，都需要清空之前的节点信息，重新写入当前存活的节点信息
     */
    @Override
    protected void processBefore() {
        treeMap.clear();
    }
}
