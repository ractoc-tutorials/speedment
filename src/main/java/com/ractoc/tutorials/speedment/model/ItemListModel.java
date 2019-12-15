package com.ractoc.tutorials.speedment.model;

import com.ractoc.tutorials.speedment.db.speedment.my_collection.item.generated.GeneratedItem;
import com.ractoc.tutorials.speedment.validators.UuidPattern;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Getter
@Setter // needed for unmarshalling
@NoArgsConstructor // needed for unmarshalling
@AllArgsConstructor // needed for builder
@Builder(toBuilder = true)
public class ItemListModel {
    @UuidPattern
    private String id;
    @NotNull
    @Length(max = 45)
    private String name;
    @NotNull
    private GeneratedItem.ItemType itemType = GeneratedItem.ItemType.BOARDGAMES;
}
