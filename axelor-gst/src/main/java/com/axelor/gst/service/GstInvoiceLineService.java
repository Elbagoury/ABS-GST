package com.axelor.gst.service;

import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.TaxLine;

public interface GstInvoiceLineService {
  public TaxLine setGstOnTaxLine(InvoiceLine invoiceLine);
}
