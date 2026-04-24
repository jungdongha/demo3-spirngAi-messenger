package com.example.demo3springaimessenger.global.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    private static final String SCHEMA_NAME = "BearerAuth";

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("Obigo AI Messenger Admin API")
                .description("Obigo AI Messenger 서비스 관리 백엔드 API 명세서입니다.")
                .version("v1.0.0");

        String jwtSchemeName = "jwtAuth";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);

        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .name(jwtSchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"))
                .addSchemas("VehicleBaseMessageDto", createBaseMessageSchema())
                .addSchemas("VehicleLocationUpdateDto", createLocationUpdateSchema())
                .addSchemas("VehicleStatusUpdateDto", createStatusUpdateSchema())
                .addSchemas("VehicleAlertDto", createAlertSchema())
                .addSchemas("VehicleCommandDto", createCommandSchema())
                .addSchemas("VehicleNotificationDto", createNotificationSchema());

        OpenAPI openapi = new OpenAPI()
                .info(info)
                .addServersItem(new Server().url("/"))
                .addSecurityItem(securityRequirement)
                .components(components)
                .addTagsItem(new Tag().name("[MQTT Specification]")
                        .description("차량과 서버 간의 MQTT 메시지 규격입니다. (Publish: 차량->서버, Subscribe: 서버->차량)"));

        // MQTT 가상 경로 주입
        openapi.setPaths(createMqttPaths());

        return openapi;
    }

    private Schema<?> createBaseMessageSchema() {
        return new ObjectSchema()
                .addProperty("vehicleUuid",
                        new StringSchema().description("차량 고유 UUID").example("550e8400-e29b-41d4-a716-446655440000"))
                .addProperty("timestamp",
                        new StringSchema().description("메시지 발생 시각 (ISO 8601)").example("2026-03-18T15:30:00+09:00"));
    }

    private Schema<?> createLocationUpdateSchema() {
        return createBaseMessageSchema()
                .description("차량 실시간 위경도 전송 (MQTT Publish)")
                .addProperty("location", new ObjectSchema()
                        .addProperty("latitude", new NumberSchema().example(37.5665))
                        .addProperty("longitude", new NumberSchema().example(126.9780)));
    }

    private Schema<?> createStatusUpdateSchema() {
        return createBaseMessageSchema()
                .description("차량 상태 업데이트 데이터. **필요한 필드만 포함하여 부분 업데이트(Partial Update)가 가능합니다.**")
                .addProperty("status", new ObjectSchema()
                        .addProperty("isEngineOn", new BooleanSchema().description("시동 상태").example(true))
                        .addProperty("isAirConditionerOn",
                                new BooleanSchema().description("공조(에어컨/히터) 상태").example(true))
                        .addProperty("isDoorLocked", new BooleanSchema().description("문 잠금 상태").example(true))
                        .addProperty("isWindowLocked", new BooleanSchema().description("창문 잠금 상태").example(true))
                        .addProperty("isHazardLightsOn", new BooleanSchema().description("비상등 점멸 상태").example(false))
                        .addProperty("fuelLevel", new NumberSchema().description("연료 잔량 (%)").example(75.5))
                        .addProperty("batteryLevel", new NumberSchema().description("배터리 잔량 (%)").example(90))
                        .addProperty("totalMileage", new NumberSchema().description("누적 주행거리 (km)").example(12345.6))
                        .addProperty("insideTemperature", new NumberSchema().description("실내 온도 (°C)").example(22.5))
                        .addProperty("outsideTemperature", new NumberSchema().description("실외 온도 (°C)").example(15.0))
                        .addProperty("tirePressure", new NumberSchema().description("평균 타이어 공기압 (PSI)").example(32))
                        .addProperty("tirePressureFl", new NumberSchema().description("앞 왼쪽 타이어 공기압 (PSI)").example(32))
                        .addProperty("tirePressureFr",
                                new NumberSchema().description("앞 오른쪽 타이어 공기압 (PSI)").example(31))
                        .addProperty("tirePressureRl", new NumberSchema().description("뒤 왼쪽 타이어 공기압 (PSI)").example(33))
                        .addProperty("tirePressureRr",
                                new NumberSchema().description("뒤 오른쪽 타이어 공기압 (PSI)").example(32))
                        .addProperty("destinationName", new StringSchema().description("목적지 명칭").example("서울역"))
                        .addProperty("destinationLatitude", new NumberSchema().description("목적지 위도").example(37.5547))
                        .addProperty("destinationLongitude", new NumberSchema().description("목적지 경도").example(126.9706))
                        .addProperty("estimatedRange", new NumberSchema().description("주행 가능 거리 (km)").example(380.0))
                        .addProperty("fuelEfficiency", new NumberSchema().description("연비 (km/L)").example(12.5)));
    }

    private Schema<?> createAlertSchema() {
        return createBaseMessageSchema()
                .description("차량 고장 알림 데이터")
                .addProperty("alertType", new StringSchema().example("DTC"))
                .addProperty("alertData", new StringSchema().example("P0303"));
    }

    private Schema<?> createCommandSchema() {
        return createBaseMessageSchema()
                .description("서버에서 차량으로 전송하는 제어 명령 (Vehicle Subscribe)")
                .addProperty("command",
                        new StringSchema().description("제어 명령 타입 (DOOR_LOCK, HVAC 등)").example("DOOR_LOCK"))
                .addProperty("params",
                        new StringSchema().description("추가 파라미터 (JSON string)").example("{\"temp\": 24}"));
    }

    private Schema<?> createNotificationSchema() {
        return createBaseMessageSchema()
                .description("서버에서 차량으로 전송하는 텍스트 알림/메시지 (Vehicle Subscribe)")
                .addProperty("title", new StringSchema().description("발신자 이름").example("karin"))
                .addProperty("body", new StringSchema().description("알림 내용").example("안녕하세요!"))
                .addProperty("category", new StringSchema().description("알림 카테고리").example("CHAT"))
                .addProperty("roomName", new StringSchema().description("채팅방 식별 ID").example("a35a0579-1b3e-4b09-be50-0eae39164d67"))
                .addProperty("roomNaturalName", new StringSchema().description("채팅방 표시 명칭").example("가족 채팅방"));
    }

    private Paths createMqttPaths() {
        Paths paths = new Paths();

        // 1. Location Update
        paths.addPathItem("MQTT: vehicle/{vehicleUuid}/location", createMqttPathItem(
                "[Publish] 실시간 위치 정보 전송",
                "차량의 현재 GPS 좌표를 발행합니다.",
                "VehicleLocationUpdateDto"));

        // 2. Status Update
        paths.addPathItem("MQTT: vehicle/{vehicleUuid}/status", createMqttPathItem(
                "[Publish] 차량 상태 정보 전송",
                "연료, 주행거리, 목적지 정보 등을 포함한 차량 상태를 발행합니다. **변경된 필드만 포함하여 부분 업데이트(Partial Update)가 가능합니다.**",
                "VehicleStatusUpdateDto"));

        // 3. Alert (DTC)
        paths.addPathItem("MQTT: vehicle/{vehicleUuid}/alert", createMqttPathItem(
                "[Publish] 차량 알림(DTC) 전송",
                "차량에서 발생한 고장 코드(DTC) 알림을 발행합니다.",
                "VehicleAlertDto"));

        // 4. Trip End
        paths.addPathItem("MQTT: vehicle/{vehicleUuid}/trip_end", createMqttPathItem(
                "[Publish] 운행 종료 이벤트",
                "주행이 종료되었음을 알립니다.",
                "VehicleBaseMessageDto"));

        // 5. Welcome Briefing
        paths.addPathItem("MQTT: vehicle/{vehicleUuid}/welcome_briefing", createMqttPathItem(
                "[Publish] 웰컴 브리핑 요청",
                "사용자 탑승 시 브리핑 데이터 생성을 요청합니다.",
                "VehicleBaseMessageDto"));

        // 6. Tire Check
        paths.addPathItem("MQTT: vehicle/{vehicleUuid}/tire_check", createMqttPathItem(
                "[Publish] 타이어 점검 요청 (주행 전)",
                "차량의 타이어 공기압 상태를 점검하고 필요 시 알림을 생성합니다.",
                "VehicleBaseMessageDto"));

        // 6-1. Tire Danger
        paths.addPathItem("MQTT: vehicle/{vehicleUuid}/tire_danger", createMqttPathItem(
                "[Publish] 타이어 위험 알림 (주행 중)",
                "주행 중 타이어 공기압 저하가 감지되었을 때 긴급 알림을 보냅니다.",
                "VehicleBaseMessageDto"));

        // 6-2. Tire Check After Trip
        paths.addPathItem("MQTT: vehicle/{vehicleUuid}/tire_check_after_trip", createMqttPathItem(
                "[Publish] 타이어 점검 요청 (주행 후)",
                "주행 종료 후 타이어 상태를 확인하고 정비소 연결을 제안합니다.",
                "VehicleBaseMessageDto"));

        // 7. [Subscribe] Command
        paths.addPathItem("MQTT: vehicle/{vehicleUuid}/command", createMqttPathItem(
                "[Subscribe] 차량 제어 명령 수신",
                "차량에 원격 제어 명령(문 잠금/해제, 시동 등)을 전송합니다.\n\n" +
                        "MQTT (vehicle/{uuid}/command)를 통해 차량으로 즉시 전달됩니다.\n" +
                        "[사용 가능한 명령어]\n" +
                        "시동/공조 제어:\n" +
                        "- REMOTE_START: 원격 시동 및 공조 켜기 (통합 제어)\n" +
                        "- REMOTE_STOP: 원격 시동 및 공조 끄기 (통합 제어)\n" +
                        "- ENGINE_START: 엔진 시동 켬\n" +
                        "- ENGINE_STOP: 엔진 시동 끔\n" +
                        "- AC_ON: 에어컨 켜기\n" +
                        "- AC_OFF: 에어컨 끄기\n" +
                        "문 제어:\n" +
                        "- DOOR_LOCK: 문 잠금\n" +
                        "- DOOR_UNLOCK: 문 잠금 해제\n" +
                        "창문 제어:\n" +
                        "- WINDOW_OPEN: 모든 창문 열기\n" +
                        "- WINDOW_CLOSE: 모든 창문 닫기\n" +
                        "비상등 제어:\n" +
                        "- HAZARD_LIGHTS_ON: 비상등 켜기\n" +
                        "- HAZARD_LIGHTS_OFF: 비상등 끄기\n" +
                        "정보 및 기타:\n" +
                        "- STATUS_QUERY: 차량 현재 상태 즉시 조회 (연료량, 주행거리 등)\n" +
                        "- VEHICLE_DIAGNOSTIC: 차량 전체 진단 실행\n" +
                        "- SEND_DESTINATION: 목적지 정보 전송 (params에 목적지 정보 포함)\n" +
                        "- NAV_INFO_QUERY: 현재 경로 정보 조회",
                "VehicleCommandDto"));

        // 8. [Subscribe] Notification
        paths.addPathItem("MQTT: vehicle/{vehicleUuid}/notification", createMqttPathItem(
                "[Subscribe] 실시간 메시지/알림 수신",
                "서버로부터 채팅 메시지나 시스템 알림을 수신합니다. (IVI 알림 센터 표시용)\n\n" +
                        "MQTT (vehicle/{uuid}/notification)를 통해 차량으로 즉시 전달됩니다.\n" +
                        "[알림 카테고리별 주요 케이스]\n" +
                        "1. CHAT (채팅): 일반 사용자나 관리자가 보낸 메시지\n" +
                        "```json\n" +
                        "{\n" +
                        "  \"vehicleUuid\": \"3b3a67ae-5020-4f3c-bd65-f1af317f9a9b\",\n" +
                        "  \"timestamp\": \"2026-03-23T17:21:05.983466\",\n" +
                        "  \"title\": \"chattest01\",\n" +
                        "  \"body\": \"테스트 메시지\",\n" +
                        "  \"category\": \"CHAT\",\n" +
                        "  \"roomName\": \"a35a0579-1b3e-4b09-be50-0eae39164d67\",\n" +
                        "  \"roomNaturalName\": \"chattest01,karin\"\n" +
                        "}\n" +
                        "```\n" +
                        "2. DRIVING_REPORT (리포트): 운행 통계 알림\n" +
                        "```json\n" +
                        "{\n" +
                        "  \"vehicleUuid\": \"...\",\n" +
                        "  \"timestamp\": \"2026-03-20T16:56:10.898...\",\n" +
                        "  \"title\": \"주행 리포트\",\n" +
                        "  \"body\": \"오늘의 주행 기록을 확인해 보세요.\",\n" +
                        "  \"category\": \"DRIVING_REPORT\"\n" +
                        "}\n" +
                        "```\n" +
                        "3. TIRE_CHECK (점검): 타이어 공기압 점검 권유 알림\n" +
                        "```json\n" +
                        "{\n" +
                        "  \"vehicleUuid\": \"...\",\n" +
                        "  \"timestamp\": \"2026-03-20T17:01:20.494...\",\n" +
                        "  \"title\": \"타이어 공기압 점검 필요\",\n" +
                        "  \"body\": \"앞쪽 왼쪽 타이어의 공기압이 권장 수치보다 낮습니다.\",\n" +
                        "  \"category\": \"TIRE_CHECK\"\n" +
                        "}\n" +
                        "```",
                "VehicleNotificationDto"));

        return paths;
    }

    private PathItem createMqttPathItem(String summary, String description, String schemaName) {
        Operation operation = new Operation()
                .tags(List.of("[MQTT Specification]"))
                .summary(summary)
                .description(description)
                .requestBody(new RequestBody()
                        .content(new Content()
                                .addMediaType("application/json", new MediaType()
                                        .schema(new Schema<>().$ref("#/components/schemas/" + schemaName)))))
                .responses(
                        new ApiResponses().addApiResponse("200", new ApiResponse().description("MQTT 발행 (성공 케이스 예시)")));

        // 실제 HTTP Method가 아닌 시각적 표시용으로 POST에 할당 (Swagger에서는 보통 POST로 표현됨)
        return new PathItem().post(operation);
    }
}