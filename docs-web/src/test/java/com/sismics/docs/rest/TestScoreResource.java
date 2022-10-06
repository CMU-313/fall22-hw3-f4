package com.sismics.docs.rest;

import java.util.Date;

import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sismics.util.filter.TokenBasedSecurityFilter;

public class TestScoreResource extends BaseJerseyTest {
    /**
     * Test the score resource.
     * 
     * @throws Exception e
    */

    private String TEST_USER_TOKEN, DOC_ID;
    
    @Before 
    public void init() {
        // Login doc
        clientUtil.createUser("testUser");
        TEST_USER_TOKEN = clientUtil.login("testUser");

        // Create a tag
        JsonObject json = target().path("/tag").request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, TEST_USER_TOKEN)
                .put(Entity.form(new Form()
                        .param("name", "SuperTag")
                        .param("color", "#ffff00")), JsonObject.class);
        String tagId = json.getString("id");

        // Create a document with document1
        long createDate = new Date().getTime();
        json = target().path("/document").request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, TEST_USER_TOKEN)
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
        DOC_ID = json.getString("id");
    }

    @Test
    public void testScoreResourceReponse() throws Exception {
        // Put a score to a document
        JsonObject json = target().path("/score").request()
        .cookie(TokenBasedSecurityFilter.COOKIE_NAME, TEST_USER_TOKEN)
        .put(Entity.form(new Form()
                .param("id", DOC_ID)
                .param("score", "5")
            ), JsonObject.class);
        
        String scoreVal = json.getString("score");
        Assert.assertEquals("5", scoreVal);
        
        String reviewerVal = json.getString("reviewer");
        Assert.assertEquals("testUser", reviewerVal); 
    }
}
