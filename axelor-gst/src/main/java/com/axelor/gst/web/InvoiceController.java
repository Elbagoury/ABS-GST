package com.axelor.gst.web;

import com.axelor.apps.account.db.Invoice;
import com.axelor.gst.service.InvoiceService;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;

public class InvoiceController {

  @Inject private InvoiceService service;

  public void invoiceCalculateFieldValue(ActionRequest request, ActionResponse response) {
    Invoice invoice = request.getContext().asType(Invoice.class);
    try {
      invoice = service.invoiceCalculateFieldValue(invoice);
      response.setValue("netAmount", invoice.getNetAmount());
      response.setValue("netIgst", invoice.getNetIgst());
      response.setValue("netSgst", invoice.getNetSgst());
      response.setValue("netCgst", invoice.getNetCgst());
      response.setValue("grossAmount", invoice.getGrossAmount());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
