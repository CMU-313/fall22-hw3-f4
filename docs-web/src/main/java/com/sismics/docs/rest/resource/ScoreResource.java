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
     * @api {post} /score Add a score
     * @apiName PostComment
     * 
     * @param documentId Document ID
     * @param content Score string
     * @return Response
     */
@Path("/score")
public class ScoreResource extends BaseResource {
    @PUT 
    public Response addScore(@FormParam("id") String documentId,
            @FormParam("score") String scoreStr) {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        
        // Validate input data
        ValidationUtil.validateRequired(documentId, "id");
        int score = ValidationUtil.validateInteger(scoreStr, "score");
        // if (score < 1 || score > 5) {
        //     // error message
        // }

        // Get the document
        DocumentDao documentDao = new DocumentDao();
        Document document = documentDao.getById(documentId);
        if (document == null) {
            throw new NotFoundException();
        }

        // Update the document score
        document.setScore(scoreStr);
        documentDao.update(document, principal.getId());
        
        // Returns ok
        JsonObjectBuilder response = Json.createObjectBuilder()
                .add("reviewer", principal.getName())
                .add("score", document.getScore());
        return Response.ok().entity(response.build()).build();
    }
}
