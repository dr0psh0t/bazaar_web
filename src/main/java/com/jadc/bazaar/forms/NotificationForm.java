package com.jadc.bazaar.forms;

import lombok.Data;

@Data
public class NotificationForm {

	private int id;

	private int notificationCategory;

	private String title;

	private String publicationDate;

	private String body;

	private Boolean isDeleted;
}
