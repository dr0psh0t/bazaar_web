package com.jadc.bazaar.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jadc.bazaar.forms.NotificationForm;
import com.jadc.persistence.entity.NotificationCategories;
import com.jadc.persistence.entity.Notifications;
import com.jadc.persistence.repository.NotificationCategoryRepository;
import com.jadc.service.NotificationService;

@Controller
@RequestMapping(value = { "/admin/notification", "/{lang}/admin/notification" })
public class NotificationController {
	private static final int ENTRIES_PER_PAGE = 20;
	private static final int ACTIVE_PAGES_TOTAL = 10;
	private static final int ACTIVE_PAGES_BEFORE_CURRENT = 4;
	private NotificationService notificationService;
	private NotificationCategoryRepository notificationCategoryRepository;

	public NotificationController(NotificationService notificationService, NotificationCategoryRepository notificationCategoryRepository) {
		this.notificationService = notificationService;
		this.notificationCategoryRepository = notificationCategoryRepository;
	}

	@GetMapping("")
	public String root(Model model) {
		return notificationIndex(model);
	}

	@GetMapping("/index")
	public String notificationIndex(Model model) {
		return findAllWithPaging(1, "", model);
	}

	@GetMapping("/index/{pageNumber}")
	public String findAllWithPaging(
			@PathVariable("pageNumber") int currentPageNumber,
			@RequestParam(value = "notificationTitle", required = false) String notificationTitle,
			Model model) {

		Page<Notifications> notifications;
		int offset = currentPageNumber - 1;
		int totalPages = 0;
		long totalEntries = 0;
		int firstEntryOnPage = 0;
		int lastEntryOnPage = 0;
		int firstPageNumber = 1;
		int lastPageNumber = ACTIVE_PAGES_TOTAL;

		if (notificationTitle == null) {
			notifications = notificationService.findAll(offset, ENTRIES_PER_PAGE);
		} else {
			notifications = notificationService.search(notificationTitle, offset, ENTRIES_PER_PAGE);
			model.addAttribute("notificationTitle", notificationTitle);
		}

		if (notifications != null) {
			int pageSize = notifications.getSize();
			totalPages = notifications.getTotalPages();
			totalEntries = notifications.getTotalElements();
			firstEntryOnPage = (pageSize * offset) + 1;
			lastEntryOnPage = pageSize * offset + pageSize;

			if (totalEntries < pageSize || totalEntries < lastEntryOnPage) {
				lastEntryOnPage = (int) totalEntries;
			}
		}

		model.addAttribute("notifications", notifications);
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

		return "notification";
	}

	@GetMapping(value = { "/add", "/update/{id}" })
	public String showNotificationForm(@PathVariable(value = "id", required = false) Integer id, Model model) {

		if (id == null) {
			model.addAttribute("notification", new NotificationForm());
		} else {
			Notifications notification = notificationService.findById(id);
			NotificationForm notificationForm = new NotificationForm();

			notificationForm.setId(notification.getId());
			notificationForm.setNotificationCategory(notification.getNotificationCategory().getId());
			notificationForm.setBody(notification.getBody());
			notificationForm.setIsDeleted(notification.getIsDeleted());
			notificationForm.setTitle(notification.getTitle());
			notificationForm.setPublicationDate(notification.getPublicationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

			model.addAttribute("notification", notificationForm);
		}

		model.addAttribute("notificationCategories", notificationCategoryRepository.findAll());

		return "notification-form";
	}

	@PostMapping("/save")
	public String save(@ModelAttribute("notification") NotificationForm notificationForm, RedirectAttributes attributes) {

		NotificationCategories notificationCategory = notificationCategoryRepository.findById(notificationForm.getNotificationCategory()).orElseThrow();
		Notifications notification = notificationService.findById(notificationForm.getId());

		notification.setNotificationCategory(notificationCategory);
		notification.setIsDeleted(false);
		notification.setBody(notificationForm.getBody());
		notification.setTitle(notificationForm.getTitle());

		try {
			notification.setPublicationDate(LocalDateTime.parse(notificationForm.getPublicationDate()));
			attributes.addFlashAttribute("saveSuccess", true);

			notificationService.save(notification);
		} catch (DateTimeParseException e) {
			attributes.addFlashAttribute("saveError", true);
		}

		return "redirect:/{lang}/admin/notification";
	}

	@PostMapping("/delete")
	public String deleteRows(@RequestParam("notifications") Integer[] notifications, RedirectAttributes attributes) {
		notificationService.delete(notifications);
		attributes.addFlashAttribute("deleteSuccess", true);

		return "redirect:/{lang}/admin/notification";
	}
}
