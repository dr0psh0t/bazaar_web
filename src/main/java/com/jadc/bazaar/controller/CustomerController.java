package com.jadc.bazaar.controller;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jadc.bazaar.event.AccountEvent;
import com.jadc.bazaar.forms.CustomerForm;
import com.jadc.persistence.entity.Customers;
import com.jadc.service.CustomerService;

@Controller
@RequestMapping(value = { "/admin/account", "/{lang}/admin/account" })
public class CustomerController {

	private static final int ENTRIES_PER_PAGE = 20;
	private static final int ACTIVE_PAGES_TOTAL = 10;
	private static final int ACTIVE_PAGES_BEFORE_CURRENT = 4;

	private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

	private final ApplicationEventPublisher applicationEventPublisher;
	private final CustomerService customerService;

	@Autowired
	public CustomerController(CustomerService service, ApplicationEventPublisher applicationEventPublisher) {
		this.customerService = service;
		this.applicationEventPublisher = applicationEventPublisher;
	}

	@GetMapping("")
	public String listAll(Model model) {
		return listAllWithPagination(1, "", model);
	}

	@GetMapping("/{pageNumber}")
	public String listAllWithPagination(@PathVariable("pageNumber") int currentPageNumber,
			@RequestParam(required = false) String companyName, Model model) {
		Page<Customers> customers;
		int offset = currentPageNumber - 1;
		int totalPages = 0;
		long totalEntries = 0;
		int firstEntryOnPage = 0;
		int lastEntryOnPage = 0;
		int firstPageNumber = 1;
		int lastPageNumber = ACTIVE_PAGES_TOTAL;

		if (companyName == null) {
			customers = customerService.findAll(offset, ENTRIES_PER_PAGE);
		} else {
			customers = customerService.search(companyName, offset, ENTRIES_PER_PAGE);
			model.addAttribute("companyName", companyName);
		}

		if (customers != null) {
			int pageSize = customers.getSize();
			totalPages = customers.getTotalPages();
			totalEntries = customers.getTotalElements();
			firstEntryOnPage = (pageSize * offset) + 1;
			lastEntryOnPage = pageSize * offset + pageSize;
			if (totalEntries < pageSize || totalEntries < lastEntryOnPage) {
				lastEntryOnPage = (int) totalEntries;
			}
		}

		model.addAttribute("customers", customers);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("totalEntries", totalEntries);
		model.addAttribute("firstEntry", firstEntryOnPage);
		model.addAttribute("lastEntry", lastEntryOnPage);
		model.addAttribute("currentPageNumber", currentPageNumber);

		if (currentPageNumber > firstPageNumber + ACTIVE_PAGES_BEFORE_CURRENT) {
			firstPageNumber = currentPageNumber - ACTIVE_PAGES_BEFORE_CURRENT;
			lastPageNumber = firstPageNumber + ACTIVE_PAGES_TOTAL - 1;
		}

		if (currentPageNumber >= totalPages - ACTIVE_PAGES_BEFORE_CURRENT) {
			firstPageNumber = totalPages - ACTIVE_PAGES_TOTAL + 1;
			lastPageNumber = totalPages;
		}

		if (totalPages < ACTIVE_PAGES_TOTAL) {
			firstPageNumber = 1;
			lastPageNumber = totalPages;
		}

		if (totalPages > 0) {
			List<Integer> pageNumbers = IntStream.rangeClosed(firstPageNumber, lastPageNumber)
					.boxed()
					.collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}

		return "customers";
	}

	@GetMapping(value = { "/add", "/update/{id}" })
	public String showCustomerForm(@PathVariable(value = "id", required = false) Integer id, Model model) {
		if (id == null) {
			model.addAttribute("customer", new CustomerForm());
		} else {
			Customers customers = customerService.findById(id);
			CustomerForm customerForm = new CustomerForm();

			customerForm.setDeleted(customers.isDeleted());
			customerForm.setCompanyName(customers.getCompanyName());
			customerForm.setContractedRecords(customers.getContractedRecords());
			customerForm.setId(customers.getId());
			customerForm.setRecordsUsed(customerForm.getRecordsUsed());

			model.addAttribute("customer", customerForm);
		}

		return "customer-form";
	}

	@PostMapping("/save")
	public String save(@ModelAttribute("customer") CustomerForm customerForm) {

		Customers customer = new Customers();
		customer.setDeleted(customerForm.isDeleted());
		customer.setCompanyName(customerForm.getCompanyName());
		customer.setId(customerForm.getId());
		customer.setContractedRecords(customerForm.getContractedRecords());
		customer.setRecordsUsed(customerForm.getRecordsUsed());

		Customers savedCustomer = customerService.save(customer);
		String id = Integer.toString(savedCustomer.getId());
		AccountEvent event = new AccountEvent(this, id);

		if (savedCustomer != null) {
			applicationEventPublisher.publishEvent(event);
			logger.info("Customer Created - Customer Controller");
		}

		return "redirect:";
	}

	@PostMapping("/delete")
	public String deleteRows(@RequestParam("customers") Integer[] customers) {
		customerService.delete(customers);

		return "redirect:";
	}
}
