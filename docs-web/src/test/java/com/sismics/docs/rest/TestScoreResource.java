package com.sismics.docs.rest;

import java.util.Date;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;

import org.junit.Assert;
import org.junit.Test;

import com.sismics.util.filter.TokenBasedSecurityFilter;

public class TestScoreResource extends BaseJerseyTest {
    /**
     * Test the score resource.
     * 
     * @throws Exception e
    */
    @Test
    public void testScoreResourceReponse() throws Exception {
        // Login doc
        clientUtil.createUser("testUser");
        String testUserToken = clientUtil.login("testUser");

        // Create a tag
        JsonObject json = target().path("/tag").request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, testUserToken)
                .put(Entity.form(new Form()
                        .param("name", "SuperTag")
                        .param("color", "#ffff00")), JsonObject.class);
        String tagId = json.getString("id");

        // Create a document with document1
        long createDate = new Date().getTime();
        json = target().path("/document").request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, testUserToken)
                .put(Entity.form(new Form()
                        .param("title", "My super title document 1")
                        .param("description", "My super description for document 1")
                        .param("subject", "Subject document 1")
                        .param("identifier", "Identifier document 1")
                        .param("publisher", "Publisher document 1")
                        .param("format", "Format document 1")
                        .param("source", "Source document 1")
                        .param("type", "Software")
                        .param("coverage", "Greenland")
                        .param("rights", "Public Domain")
                        .param("tags", tagId)
                        .param("language", "eng")
                        .param("create_date", Long.toString(createDate))
                    ), JsonObject.class);
        String docId = json.getString("id");
        
        // Put a score to a document
        json = target().path("/score").request()
        .cookie(TokenBasedSecurityFilter.COOKIE_NAME, testUserToken)
        .put(Entity.form(new Form()
                .param("id", docId)
                .param("score", "5")
            ), JsonObject.class);
        
        // Test the response values
        String scoreVal = json.getString("score");
        Assert.assertEquals("5", scoreVal);
        
        String reviewerVal = json.getString("reviewer");
        Assert.assertEquals("testUser", reviewerVal);

        // get the list of documents 
        json = target().path("/document/" + docId)
                .request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, testUserToken)
                .get(JsonObject.class);
        
        // Test the stored score 
        String storedScore = json.getString("score");
        Assert.assertEquals("5", storedScore);
    }
}
