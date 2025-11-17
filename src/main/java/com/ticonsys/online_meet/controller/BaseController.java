package com.ticonsys.online_meet.controller;

import com.ticonsys.online_meet.dto.BaseRequest;
import com.ticonsys.online_meet.dto.Response;
import com.ticonsys.online_meet.service.BaseService;
import com.ticonsys.online_meet.service.PermissionEvaluatorService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

public class BaseController<S extends BaseService, D extends BaseRequest> {

    private final S service;
    private final String permissionName;
    private final PermissionEvaluatorService permissionEvaluatorService;

    public BaseController(S service, String permissionName, PermissionEvaluatorService permissionEvaluatorService) {
        this.service = service;
        this.permissionName = permissionName;
        this.permissionEvaluatorService = permissionEvaluatorService;
    }


    @PostMapping("/get-all")
    @Transactional(readOnly = true)
    public ResponseEntity<Response<?>> getAllData(D filterData) {
        permissionEvaluatorService.checkPermission(permissionName, "ADD", "UPDATE", "DELETE");
        return service.getAllData(filterData);
    }

    // GET by ID
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<Response<?>> getDataById(@PathVariable Long id) {
        permissionEvaluatorService.checkPermission(permissionName, "ADD", "UPDATE", "DELETE");
        return service.getDataById(id);
    }

    // POST (Create)
    @PostMapping
    @Transactional
    public ResponseEntity<Response<?>> createData(@RequestBody D data) {
        permissionEvaluatorService.checkPermission(permissionName, "ADD");
        return service.createData(data);
    }

    // PUT (Update)
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Response<?>> updateData(@PathVariable Long id, @RequestBody D updatedProduct) {
        permissionEvaluatorService.checkPermission(permissionName, "UPDATE");
        return service.updateData(id, updatedProduct);
    }

    // DELETE
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Response<?>> deleteData(@PathVariable Long id) {
        permissionEvaluatorService.checkPermission(permissionName, "DELETE");
        return service.deleteData(id);
    }

}
