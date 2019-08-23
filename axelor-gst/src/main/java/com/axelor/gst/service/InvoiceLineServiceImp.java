package com.axelor.gst.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.gst.db.State;
import java.math.BigDecimal;

public class InvoiceLineServiceImp implements InvoiceLineService {

  @Override
  public InvoiceLine calculatedFieldValue(InvoiceLine invoiceLine, Invoice invoice) {
    BigDecimal sgst = BigDecimal.ZERO,
        cgst = BigDecimal.ZERO,
        igst = BigDecimal.ZERO,
        netAmount = BigDecimal.ZERO,
        grossAmount = BigDecimal.ZERO;
    netAmount = invoiceLine.getExTaxTotal();

    if (invoice.getCompany() != null && invoice.getCompany() != null) {
      State invoiceState = invoice.getAddress().getState();
      State companyState = invoice.getCompany().getAddress().getState();
      if (invoiceState.equals(companyState)) {

        sgst =
            netAmount
                .multiply((invoiceLine.getGstRate()).divide(new BigDecimal(100)))
                .divide(new BigDecimal(2));
        cgst = sgst;
        grossAmount = netAmount.add(cgst).add(sgst);
      } else {
        igst = netAmount.multiply((invoiceLine.getGstRate()).divide(new BigDecimal(100)));
        grossAmount = netAmount.add(igst);
      }
      invoiceLine.setCgst(cgst);
      invoiceLine.setSgst(sgst);
      invoiceLine.setIgst(igst);
      invoiceLine.setGrossAmount(grossAmount);

    } else {
      invoiceLine.setCgst(cgst);
      invoiceLine.setSgst(sgst);
      invoiceLine.setIgst(igst);
      invoiceLine.setGrossAmount(grossAmount);
    }
    return invoiceLine;
  }
}
