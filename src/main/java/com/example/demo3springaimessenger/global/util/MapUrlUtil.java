package com.example.demo3springaimessenger.global.util;

import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * 지도 관련 URL 생성을 위한 유틸리티
 */
@Component
public class MapUrlUtil {

    // 서버 내부 프록시 주소 사용 (이미지 렌더링 시 서버가 네이버 API 호출)
    private static final String MAP_PROXY_URL = "/api/map/v1/preview";

    public String generateStaticMapUrl(Double lat, Double lon) {
        if (lat == null || lon == null)
            return null;
        // 서버 프록시를 통해 이미지를 서빙 (600x350, 줌 15 고정)
        return String.format(Locale.ROOT, "%s?lat=%f&lon=%f&w=600&h=350&level=15",
                MAP_PROXY_URL, lat, lon);
    }

    /**
     * 구글 지도 웹/앱 링크 URL 생성
     */
    public String generateGoogleMapLink(Double lat, Double lon) {
        if (lat == null || lon == null)
            return null;
        return String.format("https://www.google.com/maps/search/?api=1&query=%f,%f", lat, lon);
    }

    /**
     * 카카오 지도 웹 링크 URL 생성
     */
    public String generateKakaoMapLink(Double lat, Double lon) {
        if (lat == null || lon == null)
            return null;
        return String.format("https://map.kakao.com/link/map/%f,%f", lat, lon);
    }

    /**
     * 네이버 지도 웹 링크 URL 생성
     */
    public String generateNaverMapLink(Double lat, Double lon, String title) {
        if (lat == null || lon == null)
            return null;

        String searchTitle = (title != null && !title.isBlank()) ? title : "차량위치";
        // 네이버 지도 URL 형식:
        // https://map.naver.com/v5/search/{query}/pin?c={lon},{lat},15,0,0,0,dh
        return String.format("https://map.naver.com/v5/search/%s/pin?c=%f,%f,15,0,0,0,dh",
                searchTitle, lon, lat);
    }

    /**
     * 네이버 지도 길찾기(경로) 링크 URL 생성
     */
    public String generateNaverRouteLink(Double startLat, Double startLon, String startName, Double endLat,
            Double endLon, String endName) {
        if (startLat == null || startLon == null || endLat == null || endLon == null)
            return null;

        String sname = (startName != null && !startName.isBlank()) ? startName : "출발지";
        String ename = (endName != null && !endName.isBlank()) ? endName : "도착지";

        try {
            sname = URLEncoder.encode(sname, StandardCharsets.UTF_8);
            ename = URLEncoder.encode(ename, StandardCharsets.UTF_8);
        } catch (Exception e) {
        }

        // 모바일/웹 공용 길찾기 URL 형식
        return String.format(Locale.ROOT,
                "https://m.map.naver.com/route.nhn?menu=route&sname=%s&sx=%f&sy=%f&ename=%s&ex=%f&ey=%f&pathType=0&showMap=true",
                sname, startLon, startLat, ename, endLon, endLat);
    }

    /**
     * 네이버 지도 길찾기용 V5 웹 URL 생성
     */
    public String generateNaverV5RouteLink(Double startLat, Double startLon, String startName, Double endLat,
            Double endLon, String endName) {
        if (startLat == null || startLon == null || endLat == null || endLon == null)
            return null;

        String sname = (startName != null && !startName.isBlank()) ? startName : "출발지";
        String ename = (endName != null && !endName.isBlank()) ? endName : "도착지";

        try {
            sname = URLEncoder.encode(sname, StandardCharsets.UTF_8);
            ename = URLEncoder.encode(ename, StandardCharsets.UTF_8);
        } catch (Exception e) {
        }

        // V5 형식: https://map.naver.com/p/directions/경도,위도,이름/경도,위도,이름/-/car
        return String.format(Locale.ROOT,
                "https://map.naver.com/p/directions/%f,%f,%s/%f,%f,%s/-/car?c=15,0,0,0,dh",
                startLon, startLat, sname, endLon, endLat, ename);
    }

    /**
     * 네이버 지도 앱 길찾기(경로) 링크 URL 생성 (nmap scheme)
     */
    public String generateNaverAppRouteLink(Double startLat, Double startLon, String startName, Double endLat,
            Double endLon, String endName) {
        if (startLat == null || startLon == null || endLat == null || endLon == null)
            return null;

        String sname = (startName != null && !startName.isBlank()) ? startName : "출발지";
        String ename = (endName != null && !endName.isBlank()) ? endName : "도착지";

        try {
            sname = URLEncoder.encode(sname, StandardCharsets.UTF_8);
            ename = URLEncoder.encode(ename, StandardCharsets.UTF_8);
        } catch (Exception e) {
            // 인코딩 실패 시 원본 사용
        }

        return String.format(Locale.ROOT,
                "nmap://route/car?slat=%f&slng=%f&sname=%s&dlat=%f&dlng=%f&dname=%s&appname=com.obigo.messenger",
                startLat, startLon, sname, endLat, endLon, ename);
    }

    /**
     * 네이버 지도 앱 지점 표시 링크 URL 생성 (nmap scheme)
     */
    public String generateNaverAppLink(Double lat, Double lon, String name) {
        if (lat == null || lon == null)
            return null;

        String placeName = (name != null && !name.isBlank()) ? name : "위치";

        try {
            placeName = URLEncoder.encode(placeName, StandardCharsets.UTF_8);
        } catch (Exception e) {
            // 인코딩 실패 시 원본 사용
        }

        // nmap://place?lat={lat}&lng={lon}&name={name}&appname=com.obigo.messenger
        return String.format(Locale.ROOT,
                "nmap://place?lat=%f&lng=%f&name=%s&appname=com.obigo.messenger",
                lat, lon, placeName);
    }
}
