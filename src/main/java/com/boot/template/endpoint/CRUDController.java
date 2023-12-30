package com.boot.template.endpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.boot.template.database.FirestoreDao;
import com.boot.template.database.TestDTO;

import jakarta.websocket.server.PathParam;

@RestController
public class CRUDController {
    final static Logger logger = LoggerFactory.getLogger(CRUDController.class);

    @Autowired
    private FirestoreDao firestoreDao;
    
	@RequestMapping(value="/create", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<Object> create(@RequestBody TestDTO testDTO) {
		logger.info("Received request to create new TestDTO object.");
        if(firestoreDao.create(testDTO)) {
            return ResponseEntity.ok().build();
        }
		return ResponseEntity.internalServerError().build();
	}
    
	@RequestMapping(value="/read/{name}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public TestDTO read(@RequestParam String name) {
		logger.info("Received request to read {}.", name);
        return firestoreDao.read(name);
	}
    
	@RequestMapping(value="/update/{name}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<Object> create(@RequestBody TestDTO testDTO, @RequestParam String name) {
		logger.info("Received request to update {}.", name);
        if(firestoreDao.update(name, testDTO)) {
            return ResponseEntity.ok().build();
        }
		return ResponseEntity.internalServerError().build();
	}
    
	@RequestMapping(value="/delete/{name}", method = RequestMethod.DELETE, produces = "application/json")
	@ResponseBody
	public ResponseEntity<Object> delete(@RequestParam String name) {
		logger.info("Received request to delete {}.", name);
        if(firestoreDao.delete(name)) {
            return ResponseEntity.ok().build();
        }
		return ResponseEntity.internalServerError().build();
	}
}
