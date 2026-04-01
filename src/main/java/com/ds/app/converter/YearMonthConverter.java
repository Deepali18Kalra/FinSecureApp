package com.ds.app.converter;

import java.time.YearMonth;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class YearMonthConverter implements AttributeConverter<YearMonth, String>{

	@Override
	public String convertToDatabaseColumn(YearMonth attribute) {
		// TODO Auto-generated method stub
		return attribute == null ? null:attribute.toString();
	}

	@Override
	public YearMonth convertToEntityAttribute(String dbData) {
		// TODO Auto-generated method stub
		return dbData==null?null:YearMonth.parse(dbData);
	}
	

}
