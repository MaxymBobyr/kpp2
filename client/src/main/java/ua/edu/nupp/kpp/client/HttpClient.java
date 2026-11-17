package ua.edu.nupp.kpp.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpClient {

    private static final String BASE = "http://localhost:8080/server";

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public String get(String path) throws Exception {
        URL url = new URL(BASE + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        int code = conn.getResponseCode();
        if (code >= 400) {
            throw new RuntimeException("HTTP error " + code + ": " + readResponse(conn.getErrorStream()));
        }
        String body = readResponse(conn.getInputStream());
        conn.disconnect();
        return body;
    }

    public String post(String endpoint, Map<String, String> params) throws Exception {
        URL url = new URL(BASE + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);

        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, String> param : params.entrySet()) {
            if (postData.length() > 0) {
                postData.append('&');
            }
            postData.append(URLEncoder.encode(param.getKey(), StandardCharsets.UTF_8));
            postData.append('=');
            postData.append(URLEncoder.encode(param.getValue(), StandardCharsets.UTF_8));
        }

        byte[] postDataBytes = postData.toString().getBytes(StandardCharsets.UTF_8);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(postDataBytes);
        }

        int code = conn.getResponseCode();
        if (code >= 400) {
            throw new RuntimeException("HTTP error " + code + ": " + readResponse(conn.getErrorStream()));
        }
        String body = readResponse(conn.getInputStream());
        conn.disconnect();
        return body;
    }

    private String readResponse(java.io.InputStream stream) throws Exception {
        if (stream == null) {
            return "";
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        return sb.toString();
    }

    public List<Equipment> getEquipments() throws Exception {
        String json = get("/equipment");
        return mapper.readValue(json, new TypeReference<List<Equipment>>() {});
    }

    public Equipment addEquipment(Equipment equipment) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("action", "add");
        params.put("name", equipment.getName());
        params.put("model", equipment.getModel());
        params.put("serialNumber", equipment.getSerialNumber());
        params.put("status", equipment.getStatus());
        params.put("location", equipment.getLocation());
        String json = post("/equipment", params);
        return mapper.readValue(json, Equipment.class);
    }

    public Equipment updateEquipment(Equipment equipment) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("action", "update");
        params.put("id", equipment.getId().toString());
        params.put("name", equipment.getName());
        params.put("model", equipment.getModel());
        params.put("serialNumber", equipment.getSerialNumber());
        params.put("status", equipment.getStatus());
        params.put("location", equipment.getLocation());
        String json = post("/equipment", params);
        return mapper.readValue(json, Equipment.class);
    }

    public void deleteEquipment(Long id) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("action", "delete");
        params.put("id", id.toString());
        post("/equipment", params);
    }

    public List<Specialist> getSpecialists() throws Exception {
        String json = get("/specialist");
        return mapper.readValue(json, new TypeReference<List<Specialist>>() {});
    }

    public Specialist addSpecialist(Specialist specialist) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("action", "add");
        params.put("fullName", specialist.getFullName());
        params.put("specialization", specialist.getSpecialization());
        params.put("phone", specialist.getPhone());
        params.put("availabilityStatus", specialist.getAvailabilityStatus());
        String json = post("/specialist", params);
        return mapper.readValue(json, Specialist.class);
    }

    public Specialist updateSpecialist(Specialist specialist) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("action", "update");
        params.put("id", specialist.getId().toString());
        params.put("fullName", specialist.getFullName());
        params.put("specialization", specialist.getSpecialization());
        params.put("phone", specialist.getPhone());
        params.put("availabilityStatus", specialist.getAvailabilityStatus());
        String json = post("/specialist", params);
        return mapper.readValue(json, Specialist.class);
    }

    public void deleteSpecialist(Long id) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("action", "delete");
        params.put("id", id.toString());
        post("/specialist", params);
    }

    public List<ServiceRequest> getServiceRequests() throws Exception {
        String json = get("/service-request");
        return mapper.readValue(json, new TypeReference<List<ServiceRequest>>() {});
    }

    public ServiceRequest addServiceRequest(ServiceRequest request) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("action", "add");
        params.put("equipmentId", String.valueOf(request.getEquipmentId()));
        params.put("description", request.getDescription());
        params.put("priority", request.getPriority());
        params.put("state", request.getState().name());
        params.put("createdDate", request.getCreatedDate().toString());
        String json = post("/service-request", params);
        return mapper.readValue(json, ServiceRequest.class);
    }

    public ServiceRequest updateServiceRequest(ServiceRequest request) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("action", "update");
        params.put("id", request.getId().toString());
        params.put("equipmentId", String.valueOf(request.getEquipmentId()));
        params.put("description", request.getDescription());
        params.put("priority", request.getPriority());
        params.put("state", request.getState().name());
        params.put("createdDate", request.getCreatedDate().toString());
        String json = post("/service-request", params);
        return mapper.readValue(json, ServiceRequest.class);
    }

    public void deleteServiceRequest(Long id) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("action", "delete");
        params.put("id", id.toString());
        post("/service-request", params);
    }

    public ServiceRequest changeServiceRequestState(Long id, ServiceRequestState state) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("action", "transition");
        params.put("id", id.toString());
        params.put("state", state.name());
        String json = post("/service-request", params);
        return mapper.readValue(json, ServiceRequest.class);
    }

    public List<ServiceRecord> getServiceRecords() throws Exception {
        String json = get("/service-record");
        return mapper.readValue(json, new TypeReference<List<ServiceRecord>>() {});
    }

    public ServiceRecord addServiceRecord(ServiceRecord record) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("action", "add");
        params.put("requestId", String.valueOf(record.getRequestId()));
        params.put("specialistId", String.valueOf(record.getSpecialistId()));
        params.put("workDescription", record.getWorkDescription());
        params.put("completionDate", record.getCompletionDate().toString());
        params.put("timeSpent", String.valueOf(record.getTimeSpent()));
        String json = post("/service-record", params);
        return mapper.readValue(json, ServiceRecord.class);
    }

    public ServiceRecord updateServiceRecord(ServiceRecord record) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("action", "update");
        params.put("id", record.getId().toString());
        params.put("requestId", String.valueOf(record.getRequestId()));
        params.put("specialistId", String.valueOf(record.getSpecialistId()));
        params.put("workDescription", record.getWorkDescription());
        params.put("completionDate", record.getCompletionDate().toString());
        params.put("timeSpent", String.valueOf(record.getTimeSpent()));
        String json = post("/service-record", params);
        return mapper.readValue(json, ServiceRecord.class);
    }

    public void deleteServiceRecord(Long id) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("action", "delete");
        params.put("id", id.toString());
        post("/service-record", params);
    }
}
