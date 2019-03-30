package PetSitters.service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.gridfs.GridFSDBFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsCriteria;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class GridFS {

    private GridFsTemplate gridFsTemplate;

    @Autowired
    public void FileController(GridFsTemplate gridFsTemplate) {
        this.gridFsTemplate = gridFsTemplate;
    }

    public String saveFile(MultipartFile file, String username) throws IOException {
        Timestamp now=new Timestamp(System.currentTimeMillis());
        String time=now.toString();
        String filename=username+"_"+time;
        System.out.println(filename);
        DBObject metaData = new BasicDBObject();
        metaData.put("status", "active");
        String extension=file.getContentType();
        String[] content=extension.split("/");
        filename=filename+"."+content[1];
        gridFsTemplate.store(file.getInputStream(), filename, file.getContentType(), metaData);
        return filename;
    }

    public GridFsResource getFile(String filename) {
        System.out.println(filename);
        GridFsResource file=gridFsTemplate.getResource(filename);
        return file;
    }


    private static Query getFilenameQuery(String name) {
        return Query.query(GridFsCriteria.whereFilename().is(name));
    }
}
