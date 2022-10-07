package com.sismics.docs.rest.resource;

import com.sismics.docs.core.constant.PermType;
import com.sismics.docs.core.dao.AclDao;
import com.sismics.docs.core.dao.CommentDao;
import com.sismics.docs.core.dao.DocumentDao;
import com.sismics.docs.core.dao.dto.CommentDto;
import com.sismics.docs.core.model.jpa.Comment;
import com.sismics.docs.core.model.jpa.Document;
import com.sismics.rest.exception.ForbiddenClientException;
import com.sismics.rest.util.ValidationUtil;
import com.sismics.util.ImageUtil;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;



/**
     * Add a comment.
     *
     * @api {put} /score Add a score
     * @apiName PostComment
     * 
     * @param documentId Document ID
     * @param content Score string
     * @return Response
     */
@Path("/score")
public class ScoreResource extends BaseResource {
    private float calculateAvgScore(int score1, int score2, float score3) {
        return (score1 + score2 + score3) / 3;
    }

    @PUT 
    @Path("/skillScore")
    public Response addSkillScore(@FormParam("id") String documentId,
            @FormParam("skillScore") String scoreStr) {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        System.out.println("skill score: " + scoreStr);
        
        // Validate input data
        ValidationUtil.validateRequired(documentId, "id");
        int score = ValidationUtil.validateInteger(scoreStr, "skillScore");

        // Get the document
        DocumentDao documentDao = new DocumentDao();
        Document document = documentDao.getById(documentId);
        if (document == null) {
            throw new NotFoundException();
        }

        // Update the document skill score
        document.setSkillScore(scoreStr);
        
        // Calculate for avg score, if any score is not null, set it to 0
        if (document.getExperienceScore() == null) {
            document.setExperienceScore("0");
        }
        if (document.getGPA() == null) {
            document.setGPA("0");
        }
        int experienceScore = Integer.parseInt(document.getExperienceScore());
        float GPAScore = Float.parseFloat(document.getGPA());

        float curScore = calculateAvgScore(score, experienceScore, GPAScore);

        documentDao.update(document, principal.getId());
        // Returns ok
        JsonObjectBuilder response = Json.createObjectBuilder()
                .add("reviewer", principal.getName())
                .add("score", curScore);
        return Response.ok().entity(response.build()).build();
    }

    
    @PUT 
    @Path("/experienceScore")
    public Response addExperienceScore(@FormParam("id") String documentId,
            @FormParam("experienceScore") String scoreStr) {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        System.out.println("experience score: " + scoreStr);
        // Validate input data
        ValidationUtil.validateRequired(documentId, "id");
        int score = ValidationUtil.validateInteger(scoreStr, "score");

        // Get the document
        DocumentDao documentDao = new DocumentDao();
        Document document = documentDao.getById(documentId);
        if (document == null) {
            throw new NotFoundException();
        }

        // Update the document experience score
        document.setExperienceScore(scoreStr);

        // Calculate for avg score, if any score is not null, set it to 0
        if (document.getSkillScore() == null) {
            document.setSkillScore("0");
        }
        if (document.getGPA() == null) {
            document.setGPA("0");
        }
        int skillScore = Integer.parseInt(document.getSkillScore());
        float GPAScore = Float.parseFloat(document.getGPA());

        float curScore = calculateAvgScore(score, skillScore, GPAScore);

        documentDao.update(document, principal.getId());
        
        // Returns ok
        JsonObjectBuilder response = Json.createObjectBuilder()
                .add("reviewer", principal.getName())
                .add("score", curScore);
        return Response.ok().entity(response.build()).build();
    }

    
    @PUT 
    @Path("/GPAScore")
    public Response addGPAScore(@FormParam("id") String documentId,
            @FormParam("GPAScore") String scoreStr) {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        System.out.println("GPA score: " + scoreStr);
        // Validate input data
        ValidationUtil.validateRequired(documentId, "id");
        // Get the document
        DocumentDao documentDao = new DocumentDao();
        Document document = documentDao.getById(documentId);
        if (document == null) {
            throw new NotFoundException();
        }
        float score = Float.parseFloat(scoreStr);

        // Update the document GPA score
        document.setGPA(scoreStr);

        // Calculate for avg score, if any score is not null, set it to 0
        if (document.getExperienceScore() == null) {
            document.setExperienceScore("0");
        }
        if (document.getSkillScore() == null) {
            document.setSkillScore("0");
        }
        int experienceScore = Integer.parseInt(document.getExperienceScore());
        int skillScore = Integer.parseInt(document.getSkillScore());

        float curScore = calculateAvgScore(skillScore, experienceScore, score);

        documentDao.update(document, principal.getId());
        
        // Returns ok
        JsonObjectBuilder response = Json.createObjectBuilder()
                .add("reviewer", principal.getName())
                .add("score", curScore);
        return Response.ok().entity(response.build()).build();
    }
}
