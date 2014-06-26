/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.integtests.invoice;

import javax.inject.Inject;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.invoice.InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003;
import org.estatio.fixture.lease.*;
import org.estatio.fixture.party.OrganisationForHelloWorld;
import org.estatio.fixture.party.OrganisationForPoison;
import org.estatio.integtests.EstatioIntegrationTest;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InvoicesTest_findOrCreateMatchingInvoice extends EstatioIntegrationTest {


    @Before
    public void setupData() {
        runScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                execute(new EstatioBaseLineFixture(), executionContext);

                execute(new LeaseItemAndTermsForOxfPoison003(), executionContext);
            }
        });
    }


    @Inject
    private Invoices invoices;
    @Inject
    private Parties parties;
    @Inject
    private Leases leases;

    private Party seller;
    private Party buyer;
    private Lease lease;
    private LocalDate invoiceStartDate;

    @Before
    public void setUp() throws Exception {
        seller = parties.findPartyByReference(OrganisationForHelloWorld.PARTY_REFERENCE);
        buyer = parties.findPartyByReference(OrganisationForPoison.PARTY_REFERENCE);
        lease = leases.findLeaseByReference(LeaseForOxfPoison003.LEASE_REFERENCE);

        invoiceStartDate = InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003.startDateFor(lease);
    }

    @Test
    public void whenDoesNotExist() {
        // given
        Assert.assertThat(invoices.allInvoices().isEmpty(), is(true));
        // when
        Invoice invoice = invoices.findOrCreateMatchingInvoice(
                seller, buyer, PaymentMethod.DIRECT_DEBIT, lease, InvoiceStatus.NEW,
                invoiceStartDate, null);
        // then
        Assert.assertNotNull(invoice);
        Assert.assertThat(invoices.allInvoices().isEmpty(), is(false));
    }

    @Test
    public void whenExist() {
        // given
        Invoice invoice = invoices.findOrCreateMatchingInvoice(
                seller, buyer, PaymentMethod.DIRECT_DEBIT, lease, InvoiceStatus.NEW,
                invoiceStartDate, null);
        // when
        Invoice invoice2 = invoices.findOrCreateMatchingInvoice(
                seller, buyer, PaymentMethod.DIRECT_DEBIT, lease, InvoiceStatus.NEW,
                invoiceStartDate, null);
        // then
        Assert.assertThat(invoice2, is(sameInstance(invoice)));
    }

}