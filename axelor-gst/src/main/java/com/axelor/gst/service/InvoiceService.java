package com.axelor.gst.service;

import com.axelor.apps.account.db.Invoice;

public interface InvoiceService {
  public Invoice invoiceCalculateFieldValue(Invoice invoice);
  // public Invoice calulateValueOnAddressChange(Invoice invoice);
}
