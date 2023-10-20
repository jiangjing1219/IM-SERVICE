package com.jiangjing.im.common.route.algorithm.consistenthash;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * 一致性 Hash 的抽象类
 *
 * @author Admin
 */
public abstract class AbstractConsistentHash {

    protected abstract void add(long hash, String value);


    protected abstract String get(String key);

    protected void sort() {
    }


    protected abstract void processBefore();

    /**
     * 根据节点集，构建 hash 环。再根据指定的key获取的相应的节点
     *
     * @param values
     * @param key
     * @return
     */
    public synchronized String process(List<String> values, String key) {
        /**
         * 预处理扩展点
         */
        processBefore();

        /**
         * 构建 hash 环
         */
        values.forEach(item -> add(hash(item), item));

        /**
         * 节点排序，扩展点
         */
        sort();

        /**
         * 获取对应的节点
         */
        return get(key);
    }

    /**
     * hash 运算
     *
     * @param value
     */
    public Long hash(String value) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 not supported", e);
        }
        md5.reset();
        byte[] keyBytes = null;
        keyBytes = value.getBytes(StandardCharsets.UTF_8);

        md5.update(keyBytes);
        byte[] digest = md5.digest();

        // hash code, Truncate to 32-bits
        long hashCode = ((long) (digest[3] & 0xFF) << 24)
                | ((long) (digest[2] & 0xFF) << 16)
                | ((long) (digest[1] & 0xFF) << 8)
                | (digest[0] & 0xFF);

        return hashCode & 0xffffffffL;
    }
}
