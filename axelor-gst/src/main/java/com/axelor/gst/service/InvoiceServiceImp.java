package com.axelor.gst.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import java.math.BigDecimal;

public class InvoiceServiceImp implements InvoiceService {

  @Override
  public Invoice invoiceCalculateFieldValue(Invoice invoice) {
    BigDecimal netAmount = BigDecimal.ZERO;
    BigDecimal netCgst = BigDecimal.ZERO;
    BigDecimal netSgst = BigDecimal.ZERO;
    BigDecimal netIgst = BigDecimal.ZERO;
    BigDecimal grossAmount = BigDecimal.ZERO;
    for (InvoiceLine invoiceLine : invoice.getInvoiceLineList()) {
      netAmount = netAmount.add(invoiceLine.getExTaxTotal());
      netCgst = netCgst.add(invoiceLine.getCgst());
      netSgst = netSgst.add(invoiceLine.getSgst());
      netIgst = netIgst.add(invoiceLine.getIgst());
    }
    grossAmount = netAmount.add(netIgst).add(netSgst).add(netCgst);
    invoice.setNetAmount(netAmount);
    invoice.setNetCgst(netCgst);
    invoice.setNetIgst(netIgst);
    invoice.setNetSgst(netSgst);
    invoice.setGrossAmount(grossAmount);
    return invoice;
  }

  //	@Override
  //	public Invoice calulateValueOnAddressChange(Invoice invoice) {
  //		List<InvoiceLine> invoiceItemList = new ArrayList<InvoiceLine>();
  //		if (invoice.getInvoiceLineList() != null) {
  //			for (InvoiceLine invoiceLine : invoice.getInvoiceLineList()) {
  //				invoiceLine = invoiceLineService.calculatedFieldValue(invoiceLine, invoice);
  //				invoiceItemList.add(invoiceLine);
  //			}
  //			invoice.setInvoiceLineList(invoiceItemList);
  //			invoice = invoiceCalculateFieldValue(invoice);
  //		}
  //		return invoice;
  //	}
}
