package com.ds.app.service.impl;

import com.ds.app.dto.request.FinanceInvestmentRequestDTO;

import com.ds.app.dto.response.FinanceInvestmentResponseDTO;

import com.ds.app.entity.FinanceInvestment;
import com.ds.app.enums.FundStatus;
import com.ds.app.exception.ResourceNotFoundException;

import com.ds.app.repository.FinanceInvestmentRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinanceInvestmentServiceImplTest {

	@Mock
	private FinanceInvestmentRepository mutualFundRepository;

	@InjectMocks
	private FinanceInvestmentServiceImpl mutualFundService;

	private FinanceInvestment mockFund;

	@BeforeEach
	void setUp() {
		mockFund = FinanceInvestment.builder().mutualFundId(1L).fundName("ICICI Prudential Bluechip")
				.fundCode("INF109K01Z13").category("Large Cap Fund").status(FundStatus.WHITELISTED).addedBy(1L).build();
	}

	@Test
	void addFund_success() throws ResourceNotFoundException {
		FinanceInvestmentRequestDTO dto = new FinanceInvestmentRequestDTO("ICICI Prudential Bluechip", "INF109K01Z13",
				"Large Cap Fund", FundStatus.WHITELISTED);

		when(mutualFundRepository.existsByFundCode("INF109K01Z13")).thenReturn(false);
		when(mutualFundRepository.existsByFundName("ICICI Prudential Bluechip")).thenReturn(false);
		when(mutualFundRepository.save(any())).thenReturn(mockFund);

		FinanceInvestmentResponseDTO result = mutualFundService.addFund(dto, 1L);

		assertThat(result).isNotNull();
		assertThat(result.getFundName()).isEqualTo("ICICI Prudential Bluechip");
		assertThat(result.getStatus()).isEqualTo(FundStatus.WHITELISTED);
	}

	@Test
	void addFund_duplicateFundCode_throwsException() {
		FinanceInvestmentRequestDTO dto = new FinanceInvestmentRequestDTO("ICICI Prudential Bluechip", "INF109K01Z13",
				"Large Cap Fund", FundStatus.WHITELISTED);

		when(mutualFundRepository.existsByFundCode("INF109K01Z13")).thenReturn(true);

		assertThatThrownBy(() -> mutualFundService.addFund(dto, 1L)).isInstanceOf(ResourceNotFoundException.class)
				.hasMessageContaining("INF109K01Z13");

		verify(mutualFundRepository, never()).save(any());
	}

	@Test
	void addFund_duplicateFundName_throwsException() {
		FinanceInvestmentRequestDTO dto = new FinanceInvestmentRequestDTO("ICICI Prudential Bluechip", "INF109K01Z13",
				"Large Cap Fund", FundStatus.WHITELISTED);

		when(mutualFundRepository.existsByFundCode(any())).thenReturn(false);
		when(mutualFundRepository.existsByFundName("ICICI Prudential Bluechip")).thenReturn(true);

		assertThatThrownBy(() -> mutualFundService.addFund(dto, 1L)).isInstanceOf(ResourceNotFoundException.class)
				.hasMessageContaining("ICICI Prudential Bluechip");
	}

	@Test
	void updateFundStatus_success() throws ResourceNotFoundException {
		when(mutualFundRepository.findById(1L)).thenReturn(Optional.of(mockFund));
		when(mutualFundRepository.save(any())).thenReturn(mockFund);

		FinanceInvestmentResponseDTO result = mutualFundService.updateFundStatus(1L, FundStatus.BLACKLISTED);

		assertThat(result).isNotNull();
		verify(mutualFundRepository).save(any());
	}

	@Test
	void updateFundStatus_notFound_throwsException() {
		when(mutualFundRepository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> mutualFundService.updateFundStatus(99L, FundStatus.BLACKLISTED))
				.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void getFundById_success() throws ResourceNotFoundException {
		when(mutualFundRepository.findById(1L)).thenReturn(Optional.of(mockFund));

		FinanceInvestmentResponseDTO result = mutualFundService.getFundById(1L);

		assertThat(result.getMutualFundId()).isEqualTo(1L);
		assertThat(result.getFundName()).isEqualTo("ICICI Prudential Bluechip");
	}

	@Test
	void getFundById_notFound_throwsException() {
		when(mutualFundRepository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> mutualFundService.getFundById(99L)).isInstanceOf(ResourceNotFoundException.class);
	}
}