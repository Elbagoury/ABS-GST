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
  public Map<String, Object> fillProductInformation(Invoice invoice, InvoiceLine invoiceLine)
      throws AxelorException {
    Map<String, Object> gstCalculation = new HashMap<>();
    gstCalculation = super.fillProductInformation(invoice, invoiceLine);
    gstCalculation.putAll(setGstOnTaxLine(invoiceLine, invoice));
    return gstCalculation;
  }

  @Transactional
  public Map<String, Object> setGstOnTaxLine(InvoiceLine invoiceLine, Invoice invoice)
      throws AxelorException {
    Map<String, Object> gstCalculation = new HashMap<>();
    boolean isPurchase = InvoiceToolService.isPurchase(invoice);
    Tax tax = Beans.get(TaxRepository.class).all().filter("self.code = 'GST'").fetchOne();
    TaxLine taxLine = tax.getActiveTaxLine();
    taxLine.setValue(invoiceLine.getProduct().getGstRate());
    invoiceLine.setTaxLine(taxLine);
    gstCalculation.put("taxLine", taxLine);
    BigDecimal sgst = BigDecimal.ZERO,
        cgst = BigDecimal.ZERO,
        igst = BigDecimal.ZERO,
        netAmount = BigDecimal.ZERO,
        grossAmount = BigDecimal.ZERO;

    netAmount = invoiceLine.getExTaxTotal();

    if (netAmount.compareTo(new BigDecimal("0.00")) == 0) {
      netAmount = getExTaxUnitPrice(invoice, invoiceLine, taxLine, isPurchase);
      gstCalculation.put("exTaxTotal", netAmount);
      invoiceLine.setExTaxTotal(netAmount);
    }
    if (invoice.getCompany() != null
        && invoice.getAddress() != null
        && invoice.getAddress().getState() != null
        && invoice.getAddress().getState() != null) {
      State invoiceState = invoice.getAddress().getState();
      State companyState = invoice.getCompany().getAddress().getState();
      if (invoiceState.equals(companyState)) {
        sgst = netAmount.multiply(invoiceLine.getTaxLine().getValue().divide(new BigDecimal(2)));
        cgst = sgst;
        grossAmount = netAmount.add(cgst).add(sgst);
      } else {
        igst = netAmount.multiply((invoiceLine.getTaxLine().getValue()));
        grossAmount = netAmount.add(igst);
      }
      gstCalculation.put("igst", igst);
      gstCalculation.put("cgst", cgst);
      gstCalculation.put("sgst", sgst);
      gstCalculation.put("grossAmount", grossAmount);
    }
    return gstCalculation;
  }
}
