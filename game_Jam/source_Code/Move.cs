using System;
using System.Collections;
using System.Collections.Generic;
using Unity.VisualScripting;
using UnityEngine;

public class Move : MonoBehaviour
{
    private Rigidbody2D rb;
    private BoxCollider2D coll;
    private SpriteRenderer sprite;
    private Animator anim;

    private float dirX;
    [SerializeField] private LayerMask jumpableGround;
    [SerializeField] private float walkSpeed;
    [SerializeField] private float jumpForce;
    [SerializeField] private float runSpeed;
    [SerializeField] private int maxJumps = 2;  // 最大跳跃次数设置为2，用于二段跳
    private int jumpCount;  // 追踪当前已跳跃次数
    [SerializeField] private AudioSource jumpSoundEffect;

    private enum MovementState {stand, running,jumping, walking}

    // Start is called before the first frame update
    void Start()
    {
        rb = GetComponent<Rigidbody2D>();
        coll = GetComponent<BoxCollider2D>();
        sprite = GetComponent<SpriteRenderer>();
        anim = GetComponent<Animator>();
    }

    // Update is called once per frame
    void Update()
    {
        float dirX = Input.GetAxisRaw("Horizontal");
        // 判断是否按下了跑步键（Shift）
        float moveSpeed = Input.GetKey(KeyCode.LeftShift) ? runSpeed : walkSpeed;
        rb.velocity = new Vector2(dirX * moveSpeed, rb.velocity.y);

        if (Input.GetButtonDown("Jump") && jumpCount < maxJumps)
        {
            jumpSoundEffect.Play();
            rb.velocity = new Vector2(rb.velocity.x, jumpForce);
            jumpCount++;
        }
        UpdateAnimationState(dirX);

        if (IsGrounded())
        {
            jumpCount = 0;
        }
    }

    //Control the walking animation
    private void UpdateAnimationState(float X)
    {
        MovementState state;

        if (X > 0f)
        {
            state = Input.GetKey(KeyCode.LeftShift) ? MovementState.running : MovementState.walking;
            sprite.flipX = false;
        }
        else if (X < 0f)
        {
            state = Input.GetKey(KeyCode.LeftShift) ? MovementState.running : MovementState.walking;
            sprite.flipX = true;
        }
        else
        {
            state = MovementState.stand;
        }
        if (rb.velocity.y > .1f)
        {
            state = MovementState.jumping;
        }
        anim.SetInteger("state", (int)state);
    }

    private bool IsGrounded()
    {
        return Physics2D.BoxCast(coll.bounds.center, coll.bounds.size, 0f, Vector2.down, .1f, jumpableGround);
    }
}
