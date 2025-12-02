package com.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.Map;
import java.util.List;

import com.backend.service.ReportService;
import com.backend.service.UserService;

@RestController
public class ReportController {
	private final ReportService reportService;
	private final UserService userService;

	public ReportController(UserService userService, ReportService reportService){
		this.userService = userService;
		this.reportService = reportService;
	}

  @PostMapping("api/listings/{listingId}/report")
  public ResponseEntity<?> reportListing(@PathVariable Long listingId, @RequestBody(required = false) Map<String,String> body, @AuthenticationPrincipal Jwt jwt){
    try{
      if (jwt == null) return ResponseEntity.status(401).body("Authentication required to report");
      String cog = jwt.getClaim("sub");
      Long userId = userService.getUserId(cog);
      String reason = (body != null && body.containsKey("reason")) ? body.get("reason") : "Reported by user";
      reportService.createReport(listingId, userId, reason);
      return ResponseEntity.status(201).body("Report submitted");
    } catch (IllegalArgumentException e){
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e){
      return ResponseEntity.internalServerError().body("An unexpected error occured" + e.getMessage());
		}
  }	
	
	@GetMapping("admin/isAdmin")
	public ResponseEntity<?> validateAdmin(@AuthenticationPrincipal Jwt jwt){
		try{
			if (jwt == null) return ResponseEntity.status(401).body("Authentication required");
			String cog = jwt.getClaim("sub");
			boolean check = userService.isAdmin(cog);
			return ResponseEntity.ok(check);
		} catch (IllegalArgumentException e){
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e){
			return ResponseEntity.internalServerError().body("An unexpected error occured" + e.getMessage());
		}
	}
	
	@GetMapping("admin/reports")
	public ResponseEntity<?> getReports(@AuthenticationPrincipal Jwt jwt){
		try{
			if (jwt == null) return ResponseEntity.status(401).body("Authentication required");
			String cog = jwt.getClaim("sub");
			Long adminId = userService.getUserId(cog);
			List<Map<String,Object>> reports = reportService.getAllReports(adminId);
			return ResponseEntity.ok(reports);
		} catch (IllegalArgumentException e){
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e){
			return ResponseEntity.internalServerError().body("An unexpected error occured" + e.getMessage());
		}
	}
	

}
