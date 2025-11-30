package com.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
public class Report {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "listing_id", nullable = false)
	private Listing listing;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(columnDefinition = "TEXT")
	private String reason;

	private LocalDateTime createdAt = LocalDateTime.now();

	public Report() {}

	public Report(Listing listing, User user, String reason) {
		this.listing = listing;
		this.user = user;
		this.reason = reason;
		this.createdAt = LocalDateTime.now();
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public Listing getListing() { return listing; }
	public void setListing(Listing listing) { this.listing = listing; }

	public User getUser() { return user; }
	public void setUser(User user) { this.user = user; }

	public String getReason() { return reason; }
	public void setReason(String reason) { this.reason = reason; }

	public LocalDateTime getCreatedAt() { return createdAt; }
	public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
