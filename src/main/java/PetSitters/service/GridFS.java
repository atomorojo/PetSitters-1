package PetSitters.service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsCriteria;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;

@Component
public class GridFS {

    private GridFsTemplate gridFsTemplate;

    private static Query getFilenameQuery(String name) {
        return Query.query(GridFsCriteria.whereFilename().is(name));
    }

    @Autowired
    public void FileController(GridFsTemplate gridFsTemplate) {
        this.gridFsTemplate = gridFsTemplate;
    }

    public String saveFile(MultipartFile file, String username) throws IOException {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        String time = now.toString();
        String filename = username + "_" + time;
        System.out.println(filename);
        DBObject metaData = new BasicDBObject();
        metaData.put("status", "active");
        gridFsTemplate.store(file.getInputStream(), filename, file.getContentType(), metaData);
        return filename;
    }

    public GridFsResource getFile(String filename) {
        System.out.println(filename);
        GridFsResource file = gridFsTemplate.getResource(filename);
        return file;
    }


    public void destroyFile(String filename) {
        System.out.println(filename);
        this.gridFsTemplate.delete(getFilenameQuery(filename));
    }
}
