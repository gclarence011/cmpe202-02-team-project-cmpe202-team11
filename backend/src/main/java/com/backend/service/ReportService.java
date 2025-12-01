package com.backend.service;

import org.springframework.stereotype.Service;
import java.util.*;

import com.backend.repository.ReportRepository;
import com.backend.repository.ListingRepository;
import com.backend.repository.UserRepository;
import com.backend.model.Report;
import com.backend.model.Listing;
import com.backend.model.User;
import com.backend.model.Role;

@Service
public class ReportService {
	private final ReportRepository reportRepository;
	private final ListingRepository listingRepository;
	private final UserRepository userRepository;

	public ReportService(ReportRepository reportRepository, ListingRepository listingRepository, UserRepository userRepository){
		this.reportRepository = reportRepository;
		this.listingRepository = listingRepository;
		this.userRepository = userRepository;
	}

	public Report createReport(Long listingId, Long userId, String reason){
		Listing listing = listingRepository.findById(listingId).orElseThrow(() -> new IllegalArgumentException("Listing not found"));
		User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
		Report report = new Report(listing, user, reason == null ? "" : reason);
		return reportRepository.save(report);
	}

	/**
	 * Returns a simplified list of reports for admin dashboard. Validates admin role.
	 */
	public List<Map<String, Object>> getAllReports(Long adminId){
		User admin = userRepository.findById(adminId).orElseThrow(() -> new IllegalArgumentException("User not found"));
		if (admin.getRole() != Role.ADMIN) {
			throw new IllegalArgumentException("Only Admin can view reports");
		}
			List<Report> reports = reportRepository.findAllByOrderByCreatedAtDesc();
			List<Map<String,Object>> out = new ArrayList<>();
			// Keep track of listing IDs we've included already
			Set<Long> includedListingIds = new HashSet<>();

			// Add actual reports first
			for (Report r : reports){
				Map<String,Object> m = new HashMap<>();
				// prefix report id to avoid collision with flagged-only synthetic ids
				m.put("id", "report-" + r.getId());
				m.put("listingId", r.getListing().getId());
				m.put("listingTitle", r.getListing().getTitle());
				m.put("listingStatus", r.getListing().getStatus() == null ? null : r.getListing().getStatus().name());
				m.put("reason", r.getReason());
				m.put("reportedBy", r.getUser().getID());
				m.put("createdAt", r.getCreatedAt());
				out.add(m);
				includedListingIds.add(r.getListing().getId());
			}

			// Also include any listings that are FLAGED even if they have no reports
			try {
				List<Listing> flagged = listingRepository.findAllByStatus(com.backend.model.Status.FLAGED);
				for (Listing l : flagged) {
					if (includedListingIds.contains(l.getId())) continue; // already included via reports
					Map<String,Object> m = new HashMap<>();
					m.put("id", "flag-" + l.getId());
					m.put("listingId", l.getId());
					m.put("listingTitle", l.getTitle());
					m.put("listingStatus", l.getStatus() == null ? null : l.getStatus().name());
					m.put("reason", "Flagged by admin");
					m.put("reportedBy", null);
					m.put("createdAt", null);
					out.add(m);
				}
			} catch (Exception e) {
				// if listingRepository doesn't support it or any error occurs, just ignore flagged extras
			}

			return out;
	}
}
