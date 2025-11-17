package com.ticonsys.online_meet.service;

import com.ticonsys.online_meet.dto.BaseRequest;
import com.ticonsys.online_meet.dto.Response;
import org.springframework.http.ResponseEntity;

public interface BaseService {
    <D extends BaseRequest> ResponseEntity<Response<?>> getAllData(D filterData);
    ResponseEntity<Response<?>> getDataById(Long id);
    <D extends BaseRequest> ResponseEntity<Response<?>> createData(D data);
    <D extends BaseRequest> ResponseEntity<Response<?>> updateData(Long id, D data);
    ResponseEntity<Response<?>> deleteData(Long id);

}
