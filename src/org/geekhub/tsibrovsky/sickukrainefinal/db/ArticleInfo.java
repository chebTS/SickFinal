/**
 * @author Cheb
 * Article entity
 */
package org.geekhub.tsibrovsky.sickukrainefinal.db;

import org.json.JSONObject;

import android.util.Log;

public class ArticleInfo {
	Long id;
	String title, linkURL, pubDate,  description; //guid,
	//String content;
	
	public ArticleInfo(JSONObject jObj) {
		super();
		title = jObj.optString("title");
		description = jObj.optString("description");
		linkURL = jObj.optString("link");
		pubDate = jObj.optString("pubDate");
		id = Long.parseLong(linkURL.substring(linkURL.lastIndexOf("/")+1));
	}
	
	public ArticleInfo(String url, String id, String title, String description) {
		super();
		this.id = Long.parseLong(id);
		this.linkURL = url;
		this.title = title;
		this.description = description;
	}

	public Long getId() {
		return id;
	}



	public String getPubDate() {
		return pubDate;
	}



	public String getDescription() {
		return description;
	}



	public String getTitle() {
		return title;
	}

	public String getLinkURL() {
		return linkURL;
	}
	
	
	
}
/*
		"title": "Day4",
        "description": {
            "p": "Waitingforadoctor.Heshowedupat9amandhasbeensomewhere(maybemorningcoffee)for40monandcounting.Ahugenumberofpeopleaccumulatedbyhisdoor.Ofcoursenoline,noorder.WhenIaskifthereisalineandmayweshouldorganizeourselvesinsomesortofline-peoplestareatmewithblankeyesasiftheyhavenoideawhatIamtalkingabout.Messagain!"
        },
        "link": "http://sickukraine.tumblr.com/post/9988303742",
        "guid": "http://sickukraine.tumblr.com/post/9988303742",
        "pubDate": "Fri,09Sep201102:43:41-0400"
*/