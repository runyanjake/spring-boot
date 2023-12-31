package com.boot.template.database;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.boot.template.endpoint.CRUDMetrics;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteBatch;
import com.google.cloud.firestore.WriteResult;

@Component
public class FirestoreDao {final static Logger logger = LoggerFactory.getLogger(FirestoreDao.class);

    private static final String testCollectionName = "testDTOs";

    @Autowired
    private Firestore firestore;
	@Autowired
	private CRUDMetrics metrics;

    public boolean create(TestDTO testDTO) {
        //Null check.
        if(testDTO == null) {
            logger.warn("Attempted to write null testDTO to db.");
            return false;
        }

        if(exists(testDTO.name)) {
            logger.info("Entry already exists for {}.", testDTO.name);
            return false;
        }

        WriteBatch batch = firestore.batch();

        DocumentReference docRef = firestore.collection(testCollectionName).document(new String(testDTO.getName()));
        batch.set(docRef, testDTO);

        ApiFuture<List<WriteResult>> future = batch.commit();

        try {
            WriteResult result = future.get().get(0);
            logger.info("Wrote to {} at time {}.", testDTO.name, result.getUpdateTime());
            metrics.countUploadedObject();
            metrics.incrementStoredObjectsCount();
            metrics.recordSummaryValue(Math.random() * 50);
        } catch(InterruptedException | ExecutionException e) {
            logger.info("Could not write to {}.", testDTO.name);
            return false;
        }
        
        return true;
    }

    public TestDTO read(String name) {
        //Null check.
        if(name == null) {
            logger.warn("Attempted to read null name from db.");
            return null;
        }

        DocumentReference docRef = firestore.collection(testCollectionName).document(name);

        ApiFuture<DocumentSnapshot> future = docRef.get();
        try {
            DocumentSnapshot document = future.get();
            if(document.exists()) {
                logger.info("Read from {}.", name);
                return TestDTO.fromMap(document.getData());
            } else {
                return null;
            }
        } catch( ExecutionException | InterruptedException e) {
            logger.error("Could not read from {}.", name, e);
            return null;
        }
    }

    public boolean update(String name, TestDTO testDTO) {
        //Null check.
        if(name == null) {
            logger.warn("Attempted to update null name in db.");
            return false;
        } else if(testDTO == null) {
            logger.warn("Attempted to update null testDTO in db.");
            return false;
        }

        //Fail if not exists
        TestDTO currentDTO = read(name);
        if(currentDTO == null) {
            return create(testDTO);
        }

        //Else, update fields that are different.
        WriteBatch batch = firestore.batch();

        DocumentReference docRef = firestore.collection(testCollectionName).document(new String(testDTO.getName()));

        if(!currentDTO.name.equals(testDTO.name)) {
            batch.update(docRef, "name", testDTO.name);
        }
        if(!currentDTO.getValue1().equals(testDTO.getValue1())) {
            batch.update(docRef, "value1", testDTO.getValue1());
        }
        if(!currentDTO.getValue2().equals(testDTO.getValue2())) {
            batch.update(docRef, "value2", testDTO.getValue2());
        }

        ApiFuture<List<WriteResult>> future = batch.commit();

        try {
            WriteResult result = future.get().get(0);
            logger.info("Wrote to {} at time {}.", testDTO.name, result.getUpdateTime());
        } catch(InterruptedException | ExecutionException e) {
            logger.info("Could not write to {}.", testDTO.name);
            return false;
        }
        
        return true;
    }

    public boolean delete(String name) {
        //Null check.
        if(name == null) {
            logger.warn("Attempted to delete null name from db.");
            return false;
        }

        WriteBatch batch = firestore.batch();

        DocumentReference docRef = firestore.collection(testCollectionName).document(name);
        batch.delete(docRef);

        ApiFuture<List<WriteResult>> future = batch.commit();

        try {
            WriteResult result = future.get().get(0);
            logger.info("Deleted at {} at time {}.", name, result.getUpdateTime());
            metrics.decrementStoredObjectsCount();
        } catch(InterruptedException | ExecutionException e) {
            logger.info("Could not delete at {}.", name);
            return false;
        }
        
        return true;
    }

    public boolean exists(String name) {
        //Null check.
        if(name == null) {
            logger.warn("Attempted to delete null name from db.");
            return false;
        }

        DocumentReference docRef = firestore.collection(testCollectionName).document(name);

        ApiFuture<DocumentSnapshot> future = docRef.get();
        try {
            DocumentSnapshot document = future.get();
            if(document.exists()) {
                logger.info("Confirmed existance of document {}.", name);
                return true;
            } else {
                return false;
            }
        } catch( ExecutionException | InterruptedException e) {
            logger.error("Could not confirm existance of document {}.", name, e);
            return false;
        }
    }
}
