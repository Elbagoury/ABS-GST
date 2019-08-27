package com.axelor.gst.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.Tax;
import com.axelor.apps.account.db.TaxLine;
import com.axelor.apps.account.db.repo.TaxLineRepository;
import com.axelor.apps.account.db.repo.TaxRepository;
import com.axelor.apps.account.service.AccountManagementAccountService;
import com.axelor.apps.account.service.AnalyticMoveLineService;
import com.axelor.apps.account.service.app.AppAccountService;
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
    // TODO Auto-generated constructor stub
  }

  @Override
  public Map<String, Object> fillPriceAndAccount(
      Invoice invoice, InvoiceLine invoiceLine, boolean isPurchase) throws AxelorException {
    // TODO Auto-generated method stub
    Map<String, Object> gstCalculation = new HashMap<>();
    gstCalculation = super.fillPriceAndAccount(invoice, invoiceLine, isPurchase);
    TaxLine taxLine=setGstOnTaxLine(invoiceLine);
    gstCalculation.put("taxLine",taxLine);
    BigDecimal sgst = BigDecimal.ZERO,
        cgst = BigDecimal.ZERO,
        igst = BigDecimal.ZERO,
        netAmount = BigDecimal.ZERO;

    BigDecimal qty = invoiceLine.getQty();
    netAmount = qty.multiply(invoiceLine.getExTaxTotal());
    System.out.println(netAmount);

    if (invoice.getCompany() != null && invoice.getCompany() != null) {
      State invoiceState = invoice.getAddress().getState();
      State companyState = invoice.getCompany().getAddress().getState();
      if (invoiceState.equals(companyState)) {

        sgst =
            netAmount
                .multiply((invoiceLine.getTaxRate()).divide(new BigDecimal(100)))
                .divide(new BigDecimal(2));
        cgst = sgst;
      } else {
        igst = netAmount.multiply((invoiceLine.getTaxRate()).divide(new BigDecimal(100)));
      }
      gstCalculation.put("igst", igst);
      gstCalculation.put("cgst", cgst);
      gstCalculation.put("sgst", sgst);
    } else {
      gstCalculation.put("igst", igst);
      gstCalculation.put("cgst", cgst);
      gstCalculation.put("sgst", sgst);
    }

    return gstCalculation;
  }

  @Override
  @Transactional
  public TaxLine setGstOnTaxLine(InvoiceLine invoiceLine) {
    // TODO Auto-generated method stub
    Tax tax = Beans.get(TaxRepository.class).all().filter("self.code = 'GST'").fetchOne();
    long taxLineId = tax.getActiveTaxLine().getId();
    System.out.println(tax);
    TaxLine taxLine =
        Beans.get(TaxLineRepository.class).all().filter("self.id = ?", taxLineId).fetchOne();
    taxLine.setValue(invoiceLine.getProduct().getGstRate());
    invoiceLine.setTaxLine(taxLine);
   
    
    return taxLine;
  }
}
