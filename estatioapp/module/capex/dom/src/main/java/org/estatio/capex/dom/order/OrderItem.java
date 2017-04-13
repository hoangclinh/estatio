package org.estatio.capex.dom.order;

import java.math.BigDecimal;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PropertyLayout;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.utils.TitleBuilder;

import org.estatio.capex.dom.items.FinancialItem;
import org.estatio.capex.dom.items.FinancialItemType;
import org.estatio.capex.dom.project.Project;
import org.estatio.dom.UdoDomainObject2;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.Property;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.tax.Tax;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "dbo",
        table = "OrderItem"
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Queries({
        @Query(
                name = "findByOrderAndCharge", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.order.OrderItem "
                        + "WHERE ordr == :ordr "
                        + "   && charge == :charge ")
})

@Unique(name = "OrderItem_order_charge_UNQ", members = { "ordr", "charge" })
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "capex.OrderItem"
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class OrderItem extends UdoDomainObject2<OrderItem> implements FinancialItem {

    public String title() {
        return TitleBuilder.start().withParent(getOrdr()).withName(getCharge()).toString();
    }

    public OrderItem() {
        super("ordr,charge");
    }

    public OrderItem(
            final Order ordr,
            final Charge charge,
            final String description,
            final BigDecimal netAmount,
            final BigDecimal vatAmount,
            final BigDecimal grossAmount,
            final Tax tax,
            final LocalDate startDate,
            final LocalDate endDate,
            final Property property,
            final Project project
    ) {
        this();
        this.ordr = ordr;
        this.charge = charge;
        this.description = description;
        this.netAmount = netAmount;
        this.vatAmount = vatAmount;
        this.grossAmount = grossAmount;
        this.tax = tax;
        this.startDate = startDate;
        this.endDate = endDate;
        this.property = property;
        this.project = project;
    }

    /**
     * Renamed from 'order' to avoid reserve keyword issues.
     */
    @Column(allowsNull = "false", name = "orderId")
    @Getter @Setter
    @PropertyLayout(named = "order")
    private Order ordr;

    @Column(allowsNull = "false", name = "chargeId")
    @Getter @Setter
    private Charge charge;

    @Column(allowsNull = "false")
    @Getter @Setter
    private String description;

    @Column(allowsNull = "false", scale = 2)
    @Getter @Setter
    private BigDecimal netAmount;

    @Column(allowsNull = "true", scale = 2)
    @Getter @Setter
    private BigDecimal vatAmount;

    @Column(allowsNull = "false", scale = 2)
    @Getter @Setter
    private BigDecimal grossAmount;

    @Column(allowsNull = "true", name = "taxId")
    @Getter @Setter
    private Tax tax;

    @Getter @Setter
    @Column(allowsNull = "false")
    private LocalDate startDate;

    @Getter @Setter
    @Column(allowsNull = "false")
    private LocalDate endDate;

    @Column(allowsNull = "true", name = "propertyId")
    @Getter @Setter
    private Property property;

    @Column(allowsNull = "true", name = "projectId")
    @Getter @Setter
    private Project project;

    @PropertyLayout(
            named = "Application Level",
            describedAs = "Determines those users for whom this object is available to view and/or modify."
    )
    @Override
    public ApplicationTenancy getApplicationTenancy() {
        return getOrdr().getApplicationTenancy();
    }



    //region > FinancialItem impl'n (not otherwise implemented by the entity's properties)
    @Override
    @Programmatic
    public BigDecimal value() {
        return getNetAmount();
    }

    @Override
    public FinancialItemType getType() {
        return FinancialItemType.ORDERED;
    }

    @Override
    public FixedAsset<?> getFixedAsset() {
        return getProperty();
    }
    //endregion


}
