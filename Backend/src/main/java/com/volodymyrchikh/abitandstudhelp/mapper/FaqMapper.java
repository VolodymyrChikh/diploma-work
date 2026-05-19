package com.volodymyrchikh.abitandstudhelp.mapper;

import com.volodymyrchikh.abitandstudhelp.domain.Faq;
import com.volodymyrchikh.abitandstudhelp.dto.FaqRequest;
import com.volodymyrchikh.abitandstudhelp.dto.FaqResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface FaqMapper {

    FaqResponse mapToFaqResponse(Faq faq);

    Faq mapToFaq(FaqRequest faqRequest);

    void updateFaqFromRequest(FaqRequest faqRequest, @MappingTarget Faq faqToUpdate);
}
