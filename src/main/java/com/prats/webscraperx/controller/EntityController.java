package com.prats.webscraperx.controller;

import com.prats.webscraperx.model.Entity;
import com.prats.webscraperx.service.EntityService;
import com.prats.webscraperx.service.PatternService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/webscraperx/api")
public class EntityController {

    @Autowired
    private EntityService entityService;

    @Autowired
    private PatternService patternService;

    @GetMapping("/getEntity/all")
    public List<Entity> getAllEntities() {
        return entityService.getList();
    }

    @GetMapping("/getEntity/{id}")
    public Entity getEntityById(Long id) {
        return entityService.getEntityById(id).orElse(null);
    }

    @GetMapping("/getEntity/byUrl")
    public List<Entity> getByUrl(String url) {
        return entityService.getByUrl(url);
    }

//    @PostMapping("/scrapeByPattern")
//    public List<Entity> scrapeByPattern(String url, String pattern) {
//    }

//    @PostMapping("/scrapeByNewPattern")
//    public String scrapeByNewPattern(String url, String pattern) {
//    }

    @DeleteMapping("/deleteEntity/{id}")
    public String deleteEntity(@PathVariable("id") Long id) {
        entityService.delete(id);
        return "Entity with ID " + id + " deleted successfully.";
    }

    @DeleteMapping("/deleteEntity/all")
    public String deleteAllEntities() {
        entityService.deleteAll();
        return "All entities deleted successfully.";
    }

    @PostMapping("/downloadAttachment/{id}")
    public String downloadAttachment(@PathVariable("id") Long id, @RequestBody String fileName, @RequestBody String downloadPath) throws IOException {
        Entity entity = entityService.getEntityById(id).orElse(null);
        if (entity != null) {
            return entityService.downloadAttachment(id, fileName, downloadPath);
        }
        return "Entity not found.";
    }

    @GetMapping("/showAttachment/{id}")
    public void showAttachment(@PathVariable("id") Long id, @RequestBody String fileName) throws IOException {
        Entity entity = entityService.getEntityById(id).orElse(null);
        if (entity != null) {
            entityService.showAttachment(id, fileName);
        } else {
            throw new RuntimeException("Entity not found.");
        }
    }
}
