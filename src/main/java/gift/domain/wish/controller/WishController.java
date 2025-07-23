package gift.domain.wish.controller;

import gift.domain.annotation.LoginMember;
import gift.domain.member.Member;
import gift.domain.wish.dto.WishPageResponse;
import gift.domain.wish.dto.WishRequest;
import gift.domain.wish.dto.WishResponse;
import gift.domain.wish.dto.WishUpdateRequest;
import gift.domain.wish.service.WishService;
import gift.global.dto.CustomPageRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wishes")
public class WishController {
    private final WishService wishService;

    public WishController(WishService wishService) {
        this.wishService = wishService;
    }

    @PostMapping
    public ResponseEntity<Void> createWish(
            @Valid @RequestBody WishRequest wishRequest,
            @LoginMember Member member
    ) {
        wishService.createWish(wishRequest, member);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateWish(
            @PathVariable Long id,
            @Valid @RequestBody WishUpdateRequest wishUpdateRequest,
            @LoginMember Member member
    ) {
        wishService.updateWish(id, wishUpdateRequest, member);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<WishResponse> deleteWish(
            @PathVariable Long id,
            @LoginMember Member member
    ) {
        wishService.deleteWish(id, member);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<WishPageResponse> getWishes(
            @LoginMember Member member,
            @Valid @ModelAttribute CustomPageRequest customPageable
            ) {
        Pageable pageable = customPageable.toPageable();
        return new ResponseEntity<>(new WishPageResponse(wishService.getWishes(member, pageable)), HttpStatus.OK);
    }
}
