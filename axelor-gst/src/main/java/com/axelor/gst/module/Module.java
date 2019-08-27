package com.axelor.gst.module;

import com.axelor.app.AxelorModule;
import com.axelor.apps.account.service.invoice.InvoiceServiceImpl;
import com.axelor.apps.account.service.invoice.generator.InvoiceGenerator;
import com.axelor.apps.businessproject.service.InvoiceServiceProjectImpl;
import com.axelor.apps.supplychain.service.InvoiceLineSupplychainService;
import com.axelor.apps.supplychain.service.invoice.InvoiceServiceSupplychainImpl;
import com.axelor.apps.supplychain.service.invoice.generator.InvoiceGeneratorSupplyChain;
import com.axelor.gst.service.GstInvoiceLineServiceImp;
import com.axelor.gst.service.GstInvoiceServiceImp;

public class Module extends AxelorModule {
  protected void configure() {
    bind(InvoiceLineSupplychainService.class).to(GstInvoiceLineServiceImp.class);
    bind(InvoiceServiceProjectImpl.class).to(GstInvoiceServiceImp.class);
  }
}
	