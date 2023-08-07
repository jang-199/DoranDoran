//package com.dorandoran.doranserver.config.redis;
//
//import org.springframework.core.io.ByteArrayResource;
//import org.springframework.core.io.Resource;
//import org.springframework.core.io.UrlResource;
//import org.springframework.data.redis.serializer.RedisSerializer;
//import org.springframework.data.redis.serializer.SerializationException;
//
//import java.net.MalformedURLException;
//import java.net.URL;
//
//public class ResourceUrlRedisSerializer implements RedisSerializer<Resource> {
//    @Override
//    public byte[] serialize(Resource resource) throws SerializationException {
//        try {
//            // Resource를 URL 문자열로 변환하여 바이트 배열로 반환
//
//            return resource.getContentAsByteArray();
////            return resource.getFile().toString().getBytes();
////            return resource.getURL().toString().getBytes();
//        } catch (Exception e) {
//            throw new SerializationException("Failed to serialize resource", e);
//        }
//    }
//
//    @Override
//    public Resource deserialize(byte[] bytes) throws SerializationException {
//        if (bytes == null) {
//            return null; // bytes가 null인 경우 null을 반환
//        }
//        // 바이트 배열로부터 URL 문자열을 생성하고 이를 UrlResource 객체로 변환하여 반환
////            String urlAsString = new String(bytes);
////            URL url = new URL(urlAsString);
////            return new UrlResource(url);
//        return new ByteArrayResource(bytes);
//    }
//}
