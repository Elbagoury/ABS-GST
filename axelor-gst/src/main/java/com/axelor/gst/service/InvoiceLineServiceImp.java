package com.axelor.gst.service;

import com.axelor.apps.account.db.InvoiceLine;
import java.math.BigDecimal;

public class InvoiceLineServiceImp implements InvoiceLineService {

  @Override
  public InvoiceLine calculatedFieldValue(InvoiceLine invoiceLine) {
    BigDecimal sgst = BigDecimal.ZERO,
        cgst = BigDecimal.ZERO,
        igst = BigDecimal.ZERO,
        netAmount = BigDecimal.ZERO,
        grossAmount = BigDecimal.ZERO;
    netAmount = invoiceLine.getExTaxTotal();
    sgst =
        netAmount
            .multiply((invoiceLine.getGstRate()).divide(new BigDecimal(100)))
            .divide(new BigDecimal(2));
    cgst = sgst;
    grossAmount = netAmount.add(cgst).add(sgst);
    igst = netAmount.multiply((invoiceLine.getGstRate()).divide(new BigDecimal(100)));
    grossAmount = netAmount.add(igst);
    invoiceLine.setCgst(cgst);
    invoiceLine.setSgst(sgst);
    invoiceLine.setIgst(igst);
    invoiceLine.setGrossAmount(grossAmount);
    return invoiceLine;
  }
}
