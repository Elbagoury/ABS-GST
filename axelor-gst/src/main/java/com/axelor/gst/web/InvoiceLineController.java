package com.axelor.gst.web;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.gst.service.InvoiceLineService;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;

public class InvoiceLineController {

	@Inject
	private InvoiceLineService service;

	public void calculatedFieldValue(ActionRequest request, ActionResponse response) {

		InvoiceLine invoiceLine = request.getContext().asType(InvoiceLine.class);
		Invoice invoice = request.getContext().getParent().asType(Invoice.class);
		try {
			InvoiceLine invoiceline = service.calculatedFieldValue(invoiceLine, invoice);
			response.setValue("igst", invoiceline.getIgst());
			response.setValue("sgst", invoiceline.getSgst());
			response.setValue("cgst", invoiceline.getCgst());
			response.setValue("grossAmount", invoiceline.getGrossAmount());
		} catch (Exception e) {
			if (invoice.getCompany() == null) {
				response.setNotify("Select Company");
			} else if (invoice.getCompany().getAddress() == null) {
				response.setNotify("Selected Company has no Address");
			} else if ((invoice.getCompany().getAddress().getState()) == null) {
				response.setNotify("Selected Company Address has no State");
			} else if (invoice.getPartner() == null) {
				response.setNotify("Select Party");
			} else if (invoice.getAddress() == null) {
				response.setNotify("Please select  Address");
			} else if ((invoice.getAddress().getState()) == null) {
				response.setNotify("Selected Invoice Address has no state");
			}
		}
	}
}
