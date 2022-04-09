package br.ce.wcaquino.taskbackend.utils;

import java.time.LocalDate;

import org.junit.Assert;
import org.junit.Test;

public class DataUtilsTest {

	@Test
	public void deveRetornarTrueParaDatasFuturas() {
		LocalDate data = LocalDate.of(2030, 8, 2);
		Assert.assertFalse(DateUtils.isEqualOrFutureDate(data));
	}
	
	@Test
	public void deveRetornarFalseParaDatasPassadas() {
		LocalDate data = LocalDate.of(2010, 8, 2);
		Assert.assertFalse(DateUtils.isEqualOrFutureDate(data));
	}
	
	@Test
	public void deveRetornarTrueParaDataPresente() {
		LocalDate data = LocalDate.now();
		Assert.assertTrue(DateUtils.isEqualOrFutureDate(data));
	}

}
