using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Drop : MonoBehaviour
{
    [SerializeField] private float speed = 4f;
    private Animator anim;
    private Rigidbody2D rb;
    private Vector2 initialPosition;
    [SerializeField] private int damage = 1;
    private bool hasDealtDamage=false;
    [SerializeField] private float damageCooldown = 3f;

    void Start()
    {
        anim = GetComponent<Animator>();
        rb = GetComponent<Rigidbody2D>();
        initialPosition = transform.position; // 保存初始位置
        StartCoroutine(MoveDrop()); // 开始移动液滴
    }

    private IEnumerator MoveDrop()
    {
        while (true)
        {
            rb.bodyType = RigidbodyType2D.Dynamic; // 使液滴处于动态状态
            yield return new WaitForSeconds(1f); // 控制下落间隔
            transform.Translate(Vector2.down * speed * Time.deltaTime);

        }
    }

    private void OnTriggerEnter2D(Collider2D collision)
    {

        if (collision.CompareTag("Ground") || collision.CompareTag("Sea"))
        {
            rb.bodyType = RigidbodyType2D.Static; // 停止移动
            anim.SetBool("Touch", true); // 播放触碰动画

            // 等待几秒后重置位置并重新开始
            StartCoroutine(ResetDrop());
        }

        if (collision.CompareTag("Player"))
        {
            PlayerLife player = collision.GetComponent<PlayerLife>();
            if (player != null && !hasDealtDamage)
            {
                player.TakeDamage(damage);
                hasDealtDamage = true;
                StartCoroutine(DamageCooldown());
            }
        }
    }

    private IEnumerator DamageCooldown()
    {
        Debug.Log("wait begin");
        yield return new WaitForSeconds(damageCooldown);
        Debug.Log("wait end");
        hasDealtDamage = false;
    }

    private IEnumerator ResetDrop()
    {
        yield return new WaitForSeconds(1f); // 等待一段时间
        transform.position = initialPosition; // 重置位置
        anim.SetBool("Touch", false);
        rb.bodyType = RigidbodyType2D.Dynamic; // 重新启用动态
    }
}

