package com.axelor.gst.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;

public interface InvoiceLineService {
  public InvoiceLine calculatedFieldValue(InvoiceLine invoiceLine,Invoice invoice);
}
