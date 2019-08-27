package com.axelor.gst.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.service.AccountManagementAccountService;
import com.axelor.apps.account.service.AnalyticMoveLineService;
import com.axelor.apps.account.service.app.AppAccountService;
import com.axelor.apps.base.service.CurrencyService;
import com.axelor.apps.base.service.PriceListService;
import com.axelor.apps.purchase.service.PurchaseProductService;
import com.axelor.apps.supplychain.service.InvoiceLineSupplychainService;
import com.axelor.exception.AxelorException;
import com.axelor.gst.db.State;
import com.google.inject.Inject;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class GstInvoiceLineServiceImp extends InvoiceLineSupplychainService {

	@Inject
	public GstInvoiceLineServiceImp(CurrencyService currencyService, PriceListService priceListService,
			AppAccountService appAccountService, AnalyticMoveLineService analyticMoveLineService,
			AccountManagementAccountService accountManagementAccountService,
			PurchaseProductService purchaseProductService) {
		super(currencyService, priceListService, appAccountService, analyticMoveLineService,
				accountManagementAccountService, purchaseProductService);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Map<String, Object> fillPriceAndAccount(Invoice invoice, InvoiceLine invoiceLine, boolean isPurchase)
			throws AxelorException {
		// TODO Auto-generated method stub
		Map<String, Object> gstCalculation = new HashMap<>();
		gstCalculation=super.fillPriceAndAccount(invoice, invoiceLine, isPurchase);

		BigDecimal sgst = BigDecimal.ZERO, cgst = BigDecimal.ZERO, igst = BigDecimal.ZERO, netAmount = BigDecimal.ZERO,
				grossAmount = BigDecimal.ZERO;
		BigDecimal qty = invoiceLine.getQty();
		netAmount = qty.multiply(invoiceLine.getPrice());
		System.out.println(netAmount);
	
		if (invoice.getCompany() != null && invoice.getCompany() != null) {
			State invoiceState = invoice.getAddress().getState();
			State companyState = invoice.getCompany().getAddress().getState();
			if (invoiceState.equals(companyState)) {

				sgst = netAmount.multiply((invoiceLine.getGstRate()).divide(new BigDecimal(100)))
						.divide(new BigDecimal(2));
				cgst = sgst;
				grossAmount = netAmount.add((cgst).multiply(new BigDecimal(2)));
			} else {
				igst = netAmount.multiply((invoiceLine.getGstRate()).divide(new BigDecimal(100)));
				System.out.println(igst);
				grossAmount = netAmount.add(igst);
			}
			gstCalculation.put("igst", igst);
			gstCalculation.put("cgst", cgst);
			gstCalculation.put("sgst", sgst);
			gstCalculation.put("grossAmount", grossAmount);
		} else {
			gstCalculation.put("igst", igst);
			gstCalculation.put("cgst", cgst);
			gstCalculation.put("sgst", sgst);
			gstCalculation.put("grossAmount", grossAmount);
		}

		return gstCalculation;
	}
}