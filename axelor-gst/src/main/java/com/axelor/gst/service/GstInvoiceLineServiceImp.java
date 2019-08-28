package com.axelor.gst.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.Tax;
import com.axelor.apps.account.db.TaxLine;
import com.axelor.apps.account.db.repo.TaxRepository;
import com.axelor.apps.account.service.AccountManagementAccountService;
import com.axelor.apps.account.service.AnalyticMoveLineService;
import com.axelor.apps.account.service.app.AppAccountService;
import com.axelor.apps.account.service.invoice.InvoiceToolService;
import com.axelor.apps.base.service.CurrencyService;
import com.axelor.apps.base.service.PriceListService;
import com.axelor.apps.purchase.service.PurchaseProductService;
import com.axelor.apps.supplychain.service.InvoiceLineSupplychainService;
import com.axelor.exception.AxelorException;
import com.axelor.gst.db.State;
import com.axelor.inject.Beans;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class GstInvoiceLineServiceImp extends InvoiceLineSupplychainService
    implements GstInvoiceLineService {

  @Inject
  public GstInvoiceLineServiceImp(
      CurrencyService currencyService,
      PriceListService priceListService,
      AppAccountService appAccountService,
      AnalyticMoveLineService analyticMoveLineService,
      AccountManagementAccountService accountManagementAccountService,
      PurchaseProductService purchaseProductService) {
    super(
        currencyService,
        priceListService,
        appAccountService,
        analyticMoveLineService,
        accountManagementAccountService,
        purchaseProductService);
  }

  @Override
	public Map<String, Object> fillProductInformation(Invoice invoice, InvoiceLine invoiceLine) throws AxelorException {
    // TODO Auto-generated method stub
	  boolean isPurchase = InvoiceToolService.isPurchase(invoice);
	  Map<String, Object> gstCalculation = new HashMap<>();
    gstCalculation = super.fillProductInformation(invoice, invoiceLine);
    TaxLine taxLine = setGstOnTaxLine(invoiceLine);
    gstCalculation.put("taxLine", taxLine);
    BigDecimal sgst = BigDecimal.ZERO,
        cgst = BigDecimal.ZERO,
        igst = BigDecimal.ZERO,
        netAmount = BigDecimal.ZERO,
        grossAmount = BigDecimal.ZERO;

    netAmount = invoiceLine.getExTaxTotal();

    if (netAmount.compareTo(new BigDecimal("0.00")) == 0) {
      // netAmount=getExTaxUnitPrice(invoice, invoiceLine, taxLine, isPurchase);
      netAmount = getExTaxUnitPrice(invoice, invoiceLine, taxLine, isPurchase);
    }
    System.out.println(netAmount);

    if (invoice.getCompany() != null && invoice.getAddress() != null) {
      State invoiceState = invoice.getAddress().getState();
      State companyState = invoice.getCompany().getAddress().getState();
      if (invoiceState.equals(companyState)) {

        sgst =
            netAmount
                .multiply((invoiceLine.getTaxRate()).divide(new BigDecimal(100)))
                .divide(new BigDecimal(2));
        cgst = sgst;
        grossAmount = netAmount.add(cgst).add(sgst);
      } else {
        igst = netAmount.multiply((invoiceLine.getTaxRate()).divide(new BigDecimal(100)));
        grossAmount = netAmount.add(igst);
      }
      gstCalculation.put("igst", igst);
      gstCalculation.put("cgst", cgst);
      gstCalculation.put("sgst", sgst);
      gstCalculation.put("grossAmount", grossAmount);
    }

    return gstCalculation;
  }

  @Override
  @Transactional
  public TaxLine setGstOnTaxLine(InvoiceLine invoiceLine) {
    // TODO Auto-generated method stub
    Tax tax = Beans.get(TaxRepository.class).all().filter("self.code = 'GST'").fetchOne();
    TaxLine taxLine = tax.getActiveTaxLine();
    taxLine.setValue(invoiceLine.getProduct().getGstRate());
    invoiceLine.setTaxLine(taxLine);
    return taxLine;
  }
}
