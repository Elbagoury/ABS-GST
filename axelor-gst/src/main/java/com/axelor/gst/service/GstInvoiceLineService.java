package com.axelor.gst.service;

import java.util.Map;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.TaxLine;
import com.axelor.exception.AxelorException;

public interface GstInvoiceLineService {
	 public Map<String, Object>  setGstOnTaxLine(InvoiceLine invoiceLine,Invoice invoice) throws AxelorException;
}
