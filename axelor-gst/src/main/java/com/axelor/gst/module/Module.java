package com.axelor.gst.module;

import com.axelor.app.AxelorModule;
import com.axelor.gst.service.InvoiceLineService;
import com.axelor.gst.service.InvoiceLineServiceImp;
import com.axelor.gst.service.InvoiceService;
import com.axelor.gst.service.InvoiceServiceImp;

public class Module extends AxelorModule {
  protected void configure() {
    bind(InvoiceLineService.class).to(InvoiceLineServiceImp.class);
    bind(InvoiceService.class).to(InvoiceServiceImp.class);
  }
}
