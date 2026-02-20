package com.volodymyrchikh.abitandstudhelp.service;

import com.volodymyrchikh.abitandstudhelp.domain.Faq;
import com.volodymyrchikh.abitandstudhelp.dto.FaqRequest;
import com.volodymyrchikh.abitandstudhelp.dto.FaqResponse;
import com.volodymyrchikh.abitandstudhelp.exception.FaqNotFoundException;
import com.volodymyrchikh.abitandstudhelp.mapper.FaqMapper;
import com.volodymyrchikh.abitandstudhelp.repository.FaqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FaqService {

    private final FaqRepository faqRepository;
    private final FaqMapper faqMapper;

    @Transactional(readOnly = true)
    public List<FaqResponse> getAllFaqs() {
        return faqRepository.findAll().stream()
                .map(faqMapper::mapToFaqResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FaqResponse getFaqById(Long id) {
        Faq faq = faqRepository.findById(id)
                .orElseThrow(() -> new FaqNotFoundException("FAQ not found with id: " + id, id));
        return faqMapper.mapToFaqResponse(faq);
    }

    @Transactional
    public FaqResponse createFaq(FaqRequest request) {
        Faq faq = faqMapper.mapToFaq(request);
        Faq savedFaq = faqRepository.save(faq);
        return faqMapper.mapToFaqResponse(savedFaq);
    }

    @Transactional
    public FaqResponse updateFaq(Long id, FaqRequest request) {
        Faq faq = faqRepository.findById(id)
                .orElseThrow(() -> new FaqNotFoundException("FAQ not found with id: " + id, id));

        faqMapper.updateFaqFromRequest(request, faq);
        Faq updatedFaq = faqRepository.save(faq);
        return faqMapper.mapToFaqResponse(updatedFaq);
    }

    @Transactional
    public void deleteFaq(Long id) {
        if (!faqRepository.existsById(id)) {
            throw new FaqNotFoundException("FAQ not found with id: " + id, id);
        }
        faqRepository.deleteById(id);
    }
}
