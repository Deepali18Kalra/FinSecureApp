package com.ds.app.service;

import java.util.List;

import com.ds.app.dto.AssetSearchResultDTO;

public interface AssetSearchReportService {

	List<AssetSearchResultDTO> searchAssets(String keyword);
}
