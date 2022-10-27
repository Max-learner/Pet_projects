package com.example.careerblog.Models;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

//Blog post class

@Entity
public class Message {
	
	//id for message
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	//message text, tag 
	@NotBlank(message = "Заполните текст статьи, пожалуйста")
	@Length(max = 32768, message = "Укоротите статью, пожалуйста. Объём - 32000 знаков.")
	private String text;
	
	@NotBlank(message = "Заполните поля, пожалуйста")
	@Length(max = 1024, message = "Укоротите текст поля, пожалуйста. Не более 1000 знаков.")
	private String tag;
	
	//message author
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	private User author;
	
	private String filename;
	
	//constructors
	public Message(String text, String tag, User user) {
		this.text = text;
		this.tag = tag;
		this.author = user;
	}

	public Message() {
	}
	
	//getters and setters
	
	public String getAuthorName() {
		return author != null ? author.getUsername() : "<no author>";
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}
	 
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	//toString for tests
	@Override
	public String toString() {
		return "Message [id=" + id + ", text=" + text + ", tag=" + tag + "]";
	}
}

