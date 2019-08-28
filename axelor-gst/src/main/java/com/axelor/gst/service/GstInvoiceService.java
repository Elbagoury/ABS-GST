package com.axelor.gst.service;

import com.axelor.apps.account.db.Invoice;

public interface GstInvoiceService {
  public Invoice setProductItem(Invoice invoice, String idList, int partyId);
}
