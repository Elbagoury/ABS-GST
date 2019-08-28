package com.axelor.gst.web;

import com.axelor.apps.account.db.Invoice;
import com.axelor.gst.service.GstInvoiceService;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;

public class GstInvoiceController {

	@Inject
	private GstInvoiceService service;

	public void setProductItem(ActionRequest request, ActionResponse response) {
		Invoice invoice = request.getContext().asType(Invoice.class);
		String idList = (String) request.getContext().get("idList");
		if (idList != null) {
			int partyId = (int) request.getContext().get("partnerId");
			Invoice invoiceSetValue = service.setProductItem(invoice, idList, partyId);
			response.setValues(invoiceSetValue);
		}
	}
}