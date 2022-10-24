package com.jadc.bazaar.forms;

import lombok.Data;

@Data
public class CustomerForm {

	private int id;

	private String companyName;

	private int recordsUsed;

	private int contractedRecords;

	private boolean isDeleted;
}
