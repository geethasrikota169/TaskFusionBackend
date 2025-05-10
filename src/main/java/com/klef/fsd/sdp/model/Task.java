package com.klef.fsd.sdp.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "task_table")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private boolean completed;
    private String status;
    private LocalDate deadline;
    private Integer priority;
    
    
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "list_id")
    private TaskList list;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public TaskList getList() {
		return list;
	}

	public void setList(TaskList list) {
		this.list = list;
	}

	public String getStatus() {
	    return status;
	}

	public void setStatus(String status) {
	    this.status = status;
	}

	public LocalDate getDeadline() {
	    return deadline;
	}

	public void setDeadline(LocalDate deadline) {
	    this.deadline = deadline;
	}

	public Integer getPriority() {
	    return priority;
	}

	public void setPriority(Integer priority) {
	    this.priority = priority;
	}
	
	@JsonProperty("listId")
	public Long getListId() {
	    return list != null ? list.getId() : null;
	}
}