package com.boot.template.endpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.boot.template.database.FirestoreDao;
import com.boot.template.database.TestDTO;

import io.micrometer.core.annotation.Timed;

@RestController
public class CRUDController {
    final static Logger logger = LoggerFactory.getLogger(CRUDController.class);

    @Autowired
    private FirestoreDao firestoreDao;
    
	@Timed(value = "create.time", description = "Time taken to complete a create API call.")
	@PostMapping(value="/create", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<Object> create(@RequestBody TestDTO testDTO) {
		logger.info("Received request to create new TestDTO object.");
        if(firestoreDao.create(testDTO)) {
            return ResponseEntity.ok().build();
        }
		return ResponseEntity.internalServerError().build();
	}
    
	@Timed(value = "read.time", description = "Time taken to complete a read API call.")
	@GetMapping(value="/read/{name}", produces = "application/json")
	@ResponseBody
	public TestDTO read(@PathVariable String name) {
		logger.info("Received request to read {}.", name);
        return firestoreDao.read(name);
	}
    
	@Timed(value = "update.time", description = "Time taken to complete an update API call.")
	@PutMapping(value="/update/{name}", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<Object> update(@RequestBody TestDTO testDTO, @PathVariable String name) {
		logger.info("Received request to update {}.", name);
        if(firestoreDao.update(name, testDTO)) {
            return ResponseEntity.ok().build();
        }
		return ResponseEntity.internalServerError().build();
	}
    
	@Timed(value = "delete.time", description = "Time taken to complete a delete API call.")
	@DeleteMapping(value="/delete/{name}", produces = "application/json")
	@ResponseBody
	public ResponseEntity<Object> delete(@PathVariable String name) {
		logger.info("Received request to delete {}.", name);
        if(firestoreDao.delete(name)) {
            return ResponseEntity.ok().build();
        }
		return ResponseEntity.internalServerError().build();
	}
}
