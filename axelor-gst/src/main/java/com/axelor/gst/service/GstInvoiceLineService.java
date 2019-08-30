package com.axelor.gst.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.exception.AxelorException;
import java.util.Map;

public interface GstInvoiceLineService {
  public Map<String, Object> setGstOnTaxLine(InvoiceLine invoiceLine, Invoice invoice)
      throws AxelorException;
}
